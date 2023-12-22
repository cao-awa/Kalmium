package com.github.cao.awa.kalmium.controller.login;

import com.github.cao.awa.kalmia.bootstrap.Kalmia;
import com.github.cao.awa.kalmia.identity.LongAndExtraIdentity;
import com.github.cao.awa.kalmia.network.packet.inbound.login.password.LoginWithPasswordPacket;
import com.github.cao.awa.kalmium.KalmiumApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class LoginSceneController {
    @FXML
    private TextArea useridArea;
    @FXML
    private TextArea passwordArea;
    @FXML
    private Button loginButton;

    @FXML
    protected void onLoginClicked() {
        Kalmia.CLIENT.router().send(new LoginWithPasswordPacket(new LongAndExtraIdentity(0, new byte[]{123}), "123456"));

        try {
            KalmiumApplication.showMessagingScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
