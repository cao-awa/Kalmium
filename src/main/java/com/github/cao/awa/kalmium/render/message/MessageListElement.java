package com.github.cao.awa.kalmium.render.message;

import com.github.cao.awa.apricot.resource.loader.ResourceLoader;
import com.github.cao.awa.kalmia.message.Message;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.EntrustEnvironment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MessageListElement extends AnchorPane {
    private final Object graphic;
    private final Message session;
    @FXML
    private Label senderNameLabel;
    @FXML
    private TextArea messageContentArea;
    @FXML
    private Label extraInfoLabel;

    public MessageListElement(Message message) {
        this.session = message;

        FXMLLoader fxmlLoader = new FXMLLoader(ResourceLoader.url("kalmium/messaging/list/element/messaging_list_element.fxml"));
        fxmlLoader.setController(this);

        try {
            this.graphic = fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.senderNameLabel.setText(message.sender()
                                            .toString() + "(Sender name)");

        this.messageContentArea.setText(message.display()
                                                .coverContent());

        this.extraInfoLabel.setText("No ext");
    }

    public <T> T graphic() {
        return EntrustEnvironment.cast(this.graphic);
    }

    public Message session() {
        return this.session;
    }
}
