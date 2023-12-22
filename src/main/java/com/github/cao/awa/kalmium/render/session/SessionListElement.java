package com.github.cao.awa.kalmium.render.session;

import com.github.cao.awa.apricot.resource.loader.ResourceLoader;
import com.github.cao.awa.kalmia.session.Session;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.EntrustEnvironment;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SessionListElement extends AnchorPane {
    private final Object graphic;
    private final Session session;
    @FXML
    private Label sessionName;

    public SessionListElement(Session session) {
        this.session = session;

        FXMLLoader fxmlLoader = new FXMLLoader(ResourceLoader.url("kalmium/session/list/element/session_list_element.fxml"));
        fxmlLoader.setController(this);

        try {
            this.graphic = fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.sessionName.setText(session.displayName());
    }

    public <T> T graphic() {
        return EntrustEnvironment.cast(this.graphic);
    }

    public Session session() {
        return this.session;
    }
}
