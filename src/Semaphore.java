import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

//public class Semaphore {
//    private int value;
//
//
//    public void do() {
//        Object.wait();
//    }
//    // => th1 | th2 | th3 | th4
//    public void release() {
//        notify();
//    }
//}

enum DeviceType {
    Mobile, PC, Tablet
}

class Device extends Thread {
    private String name;
    private DeviceType type;
    private Router router;
    private int index;
    Semaphore semaphore;

    public Device(String name, DeviceType type, Router router) {
        this.router = router;
        this.name = name;
        this.type = type;
    }

    @Override
    public void run() {

        router.addDevice(this);
        System.out.println("before connection " + name + " at index " + index);
        //router.connect();
        System.out.println("after connection " + name + " at index " + index);
        try {
            Thread.sleep(100);
        } catch (Exception e) {

        }
        // Wait random time.
        // Do some activity.
        // Wait random time.
        router.resetConnection(index);
        System.out.println("after release " + name + " at index " + index);
        router.release();

    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void logout() {

    }
}

class Router {
    Device[] devices = new Device[2];
    Semaphore semaphore = new Semaphore(2);

    int idx = 0;

    public Router() {
        for (int i = 0; i < 2; i++) {
            devices[i] = null;
        }
    }

    public void addDevice(Device d) {
        try {

            semaphore.acquire();
        } catch (Exception e) {

        }
        for (int i = 0; i < 2; i++) {
            if (devices[i] == null) {
                devices[i] = d;
                d.setIndex(i);
                break;
            }
        }

    }

    public void resetConnection(int i) {
        devices[i] = null;
    }

    public void connect() {
        try {
            for (Device d : devices) {
                d.start();
            }
            semaphore.acquire();

        } catch (Exception e) {

        }
    }

    public void release() {
        try {
            semaphore.release();
        } catch (Exception e) {

        }
    }
}

class Network {
    public static void main(String args[]) {
        Router router = new Router();

        Device d1 = new Device("d1", DeviceType.PC, router);
        Device d2 = new Device("d2", DeviceType.Mobile, router);
        Device d3 = new Device("d3", DeviceType.PC, router);
        d1.start();
        d2.start();
        d3.start();


    }
}
