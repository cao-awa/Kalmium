package com.github.cao.awa.kalmium.render.message;

import javafx.scene.control.ListCell;

public class MessageListCell extends ListCell<MessageListElement> {
    @Override
    protected void updateItem(MessageListElement item, boolean empty) {
        if (item == null) {
            return;
        }
        super.updateItem(item,
                         empty
        );
        setGraphic(item.graphic());
    }
}
