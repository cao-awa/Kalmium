package com.github.cao.awa.kalmium.controller.messaging;

import com.github.cao.awa.apricot.identifier.BytesRandomIdentifier;
import com.github.cao.awa.kalmia.bootstrap.Kalmia;
import com.github.cao.awa.kalmia.constant.KalmiaConstant;
import com.github.cao.awa.kalmia.identity.PureExtraIdentity;
import com.github.cao.awa.kalmia.network.packet.Packet;
import com.github.cao.awa.kalmia.network.packet.inbound.message.send.SendMessagePacket;
import com.github.cao.awa.kalmium.KalmiumApplication;
import com.github.cao.awa.kalmium.render.message.MessageListElement;
import com.github.cao.awa.kalmium.render.session.SessionListElement;
import com.github.cao.awa.viburnum.util.bytes.BytesUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.nio.charset.StandardCharsets;

public class MessagingSceneController {
    @FXML
    public ListView<SessionListElement> sessionList;
    @FXML
    public ListView<MessageListElement> messageList;
    @FXML
    private TextArea messageInput;
    @FXML
    private Button sendMessageButton;

    @FXML
    protected void onSendMessageClicked() {
        PureExtraIdentity sessionIdentity = this.sessionList.getSelectionModel()
                                                            .getSelectedItem()
                                                            .session()
                                                            .identity();

        byte[] receipt = Packet.createReceipt();

        KalmiumApplication.sendingMessage.put(PureExtraIdentity.create(receipt), sessionIdentity);

        Kalmia.CLIENT.router()
                     .send(new SendMessagePacket(sessionIdentity,
                                                 KalmiaConstant.UNMARKED_PURE_IDENTITY,
                                                 this.messageInput.getText()
                                                                  .getBytes(StandardCharsets.UTF_8),
                                                 KalmiaConstant.UNMARKED_PURE_IDENTITY,
                                                 BytesUtil.EMPTY,
                                                 false
                     ).receipt(receipt));

    }
}
