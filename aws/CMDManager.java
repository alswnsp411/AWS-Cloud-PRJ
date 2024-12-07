package aws;

import static config.configConstants.KEY_PATH;
import static config.configConstants.USER;

import com.jcraft.jsch.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class CMDManager {

    private boolean isSSHAvailable(String instancePublicDNS) {
        try (Socket socket = new Socket()) {
            // 22번 포트로 연결 테스트 (5초 타임아웃)
            socket.connect(new InetSocketAddress(instancePublicDNS, 22), 5000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void runInstance(final String instancePublicDNS, final String command) {
        try {
            while (!isSSHAvailable(instancePublicDNS)) {
                System.out.println("SSH not available yet. Retrying...");
                Thread.sleep(5000); // 5초 대기
            }

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
