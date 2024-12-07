package aws;

import static config.configConstants.KEY_PATH;
import static config.configConstants.USER;

import com.jcraft.jsch.*;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CMDManager {

    public void runInstance(final String instancePublicDNS, final String command) {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(KEY_PATH);
            Session session = jsch.getSession(USER, instancePublicDNS, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setErrStream(System.err);
            channel.connect();

            InputStream in = channel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
