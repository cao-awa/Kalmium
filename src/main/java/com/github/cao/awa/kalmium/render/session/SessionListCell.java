package com.github.cao.awa.kalmium.render.session;

import javafx.scene.control.ListCell;

public class SessionListCell extends ListCell<SessionListElement> {
    @Override
    protected void updateItem(SessionListElement item, boolean empty) {
        if (item == null) {
            return;
        }
        super.updateItem(item,
                         empty
        );
        setGraphic(item.graphic());
    }
}
