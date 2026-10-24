package app.aemu.stub;

import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;

import java.lang.reflect.Method;

/**
 * Binder services a firmware's own programs call without checking that they exist, where the program that would
 * register them needs the phone's hardware (Samsung's SurfaceFlinger tells "Exynos.HWCService", which lives in the
 * Exynos display HAL, that the boot is over, and dies on a null pointer without it).
 *
 * app_process /system/bin app.aemu.stub.StandIn <service name>...
 *
 * Every call is answered "no exception" and an empty reply. A name somebody has registered is left alone.
 */
public final class StandIn {
    public static void main(String[] args) throws Exception {
        Looper.prepare();
        Class<?> sm = Class.forName("android.os.ServiceManager");
        Method check = sm.getMethod("checkService", String.class);
        Method add = sm.getMethod("addService", String.class, IBinder.class);
        for (final String name : args) {
            try {
                if (check.invoke(null, name) != null) continue;
                add.invoke(null, name, new Binder() {
                    @Override protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) {
                        if (code == INTERFACE_TRANSACTION) { if (reply != null) reply.writeString(name); return true; }
                        // system_server's FilteredPreloadAppManager / ActualPreloadAppManager call the firmware's
                        // AIDL IHwFactoryInterface first. The vendor's own service CHECK-fails on the phone's
                        // secflash/oeminfo hardware, so this stand-in answers oeminfo_Read (35) with an empty
                        // IFacIntByte (result = 0, outBuf = empty): the managers stop at the AIDL branch and never
                        // fall through to the fatal HIDL 1.1 getService().
                        if (code == 35 && name.contains("hwfactoryinterface") && data != null && reply != null) {
                            data.readString();   // interface token written by the Proxy
                            data.readInt();      // idx
                            data.readInt();      // size
                            reply.writeNoException();          // no exception
                            reply.writeInt(1);                 // readTypedObject: parcelable present
                            reply.writeInt(0);                 // result = RET_SUCCESS
                            reply.writeInt(0);                 // byte[] length = 0 (empty)
                            return true;
                        }
                        if (reply != null) reply.writeNoException();
                        return true;
                    }
                });
                System.out.println("aemu-standin: " + name);
            } catch (Throwable t) { t.printStackTrace(System.out); }
        }
        System.out.flush();
        Looper.loop();
    }
}
