package com.github.cao.awa.kalmium;

import com.github.cao.awa.apricot.resource.loader.ResourceLoader;
import com.github.cao.awa.apricot.thread.pool.ExecutorFactor;
import com.github.cao.awa.apricot.util.collection.ApricotCollectionFactor;
import com.github.cao.awa.kalmia.bootstrap.Kalmia;
import com.github.cao.awa.kalmia.constant.KalmiaConstant;
import com.github.cao.awa.kalmia.identity.LongAndExtraIdentity;
import com.github.cao.awa.kalmia.identity.PureExtraIdentity;
import com.github.cao.awa.kalmia.message.Message;
import com.github.cao.awa.kalmia.message.user.UserMessage;
import com.github.cao.awa.kalmia.plugin.internal.eventbus.KalmiaEventBus;
import com.github.cao.awa.kalmia.session.Session;
import com.github.cao.awa.kalmium.controller.messaging.MessagingSceneController;
import com.github.cao.awa.kalmium.render.message.MessageListCell;
import com.github.cao.awa.kalmium.render.message.MessageListElement;
import com.github.cao.awa.kalmium.render.session.SessionListCell;
import com.github.cao.awa.kalmium.render.session.SessionListElement;
import com.github.cao.awa.kalmium.scene.KalmiumScenes;
import com.github.cao.awa.viburnum.util.bytes.BytesUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class KalmiumApplication extends Application {
    public static final ExecutorService executor = ExecutorFactor.cached();
    public static final Map<KalmiumScenes, Scene> scenes = ApricotCollectionFactor.hashMap();
    public static final Map<PureExtraIdentity, PureExtraIdentity> sendingMessage = ApricotCollectionFactor.hashMap();
    public static ObservableList<SessionListElement> sessionsList;
    public static ObservableList<MessageListElement> messagesList;
    public static Stage currentStage;
    public static Scene currentScene;

    @Override
    public void start(Stage stage) throws IOException {
        currentStage = stage;

        initLoginScene();
        initMessagingScene();

        showLoginScene();
    }

    public static void initLoginScene() throws IOException {
        if (scenes.containsKey(KalmiumScenes.LOGIN)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ResourceLoader.url("kalmium/login/login_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),
                                1200,
                                740
        );
        scenes.put(KalmiumScenes.LOGIN,
                   scene
        );
    }

    public static void showLoginScene() {
        Scene scene = scenes.get(KalmiumScenes.LOGIN);
        currentScene = scene;
        currentStage.setTitle("Kalmium");
        currentStage.setScene(scene);
        currentStage.setResizable(false);
        currentStage.show();
    }

    public static void initMessagingScene() throws IOException {
        if (scenes.containsKey(KalmiumScenes.MESSAGING)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ResourceLoader.url("kalmium/messaging/messaging_scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),
                                1200,
                                740
        );
        scenes.put(KalmiumScenes.MESSAGING,
                   scene
        );

        // Sessions list.
        ListView<SessionListElement> sessionList = ((MessagingSceneController) fxmlLoader.getController()).sessionList;

        sessionList.setCellFactory(param -> new SessionListCell());

        sessionsList = sessionList.getItems();

        KalmiaEventBus.sessionListenersUpdate.trigger((router, receipt, sessions) -> {
            sessionsList.clear();

            for (Session session : sessions) {
                System.out.printf("Session: " + session.displayName());

                sessionsList.add(new SessionListElement(session));
            }
        });

        sessionList.getSelectionModel()
                   .setSelectionMode(SelectionMode.SINGLE);

        KalmiaEventBus.sentMessage.trigger((router, receipt, seq) -> {
            PureExtraIdentity sessionIdentity = sessionList.getSelectionModel()
                                                                .getSelectedItem()
                                                                .session()
                                                                .identity();

            if (sessionIdentity.equals(sendingMessage.get(PureExtraIdentity.create(receipt)))) {
                Platform.runLater(() -> {
                    refreshNewlyMessages(sessionIdentity, 10);
                });
            }
        });

        KalmiaEventBus.sendMessageRefused.trigger((router, receipt, reason) -> {
            sendingMessage.remove(PureExtraIdentity.create(receipt));
        });

        // Messages list.
        ListView<MessageListElement> messageList = ((MessagingSceneController) fxmlLoader.getController()).messageList;

        messageList.setCellFactory(param -> new MessageListCell());

        ContextMenu menu = new ContextMenu();
        MenuItem item1 = new MenuItem("删除");
        MenuItem item2 = new MenuItem("测试1");
        MenuItem item3 = new MenuItem("Cancel");
        menu.getItems()
            .add(item1);
        menu.getItems()
            .add(item2);
        menu.getItems()
            .add(item3);

        messagesList = messageList.getItems();

        messageList.setOnContextMenuRequested(event -> {
            menu.show(messageList,
                      event.getScreenX(),
                      event.getScreenY()
            );
        });

        Consumer<MouseEvent> switchSessionHandler = event -> {
            Session session = sessionList.getSelectionModel().getSelectedItem().session();
            refreshNewlyMessages(session.identity(), 100);

            messageList.scrollTo(messageList.getItems().size() - 1);
        };

        sessionList.setOnMouseClicked(switchSessionHandler :: accept);
    }

    public static void showMessagingScene() {
        Scene scene = scenes.get(KalmiumScenes.MESSAGING);
        currentScene = scene;
        currentStage.setTitle("Kalmium");
        currentStage.setScene(scene);
        currentStage.setResizable(false);
        currentStage.show();
    }

    public static void refreshNewlyMessages(PureExtraIdentity sessionIdentity, int range) {
        messagesList.clear();
        long curSeq = Kalmia.CLIENT.curMsgSeq(sessionIdentity, true);
        for (Message message : Kalmia.CLIENT.getMessages(sessionIdentity,
                                                         Math.max(0,
                                                                  curSeq - range
                                                         ),
                                                         curSeq,
                                                         true
        )) {
            messagesList.add(new MessageListElement(message));
        }
    }

    public static void launchKalmium() {
        executor.execute(Application :: launch);
        executor.execute(() -> {
            try {
                Kalmia.startClient();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        });
    }
}