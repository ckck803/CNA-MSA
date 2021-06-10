package theater;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieManagementController {

    @GetMapping("/hpa")
    public String getHPA() {
        Random rng = new Random();
        long loopCnt = 0;

        while (loopCnt < 100) {
            double r = rng.nextFloat();
            double v = Math.sin(Math.cos(Math.sin(Math.cos(r))));
            System.out.println(String.format("r: %f, v %f", r, v));
            loopCnt++;
        }

        return "hpa";
    }

    @GetMapping("/serviceAddress")
    public String getServiceAddress () {
        String serviceAddress = null;
        if (serviceAddress == null) {
            serviceAddress = findMyHostname() + "/" + findMyIpAddress();
        }
        return serviceAddress;
    }

    private String findMyHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown host name";
        }
    }

    private String findMyIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "unknown IP address";
        }
    }
}
