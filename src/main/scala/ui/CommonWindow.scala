package ui

import akka.actor.ActorRef
import cluster.{CommonChatMsg, PrivateChatMsg, SelfPrivateChatMsg}
import javafx.event.ActionEvent
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control.{ListView, TextArea, TextField}
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.stage.Stage
import model.ChatModel


class CommonWindow(name : String, meetingManager: ActorRef, model : ChatModel) {

  def start(): Unit = {
    val textArea = new TextArea()
    val textField = new TextField()
    var userName = name
    val usersListView = new ListView[String](model.users)

    model.newMessage.addListener((_, _, newValue) => textArea.appendText(newValue))

    model.users.add(name)
    val primaryStage = new Stage()
    val grid = new GridPane
    grid.setAlignment(Pos.CENTER)
    grid.setHgap(10)
    grid.setVgap(10)
    grid.setPadding(new Insets(25, 25, 25, 25))

    usersListView
      .getSelectionModel
      .selectedItemProperty
      .addListener((_,_, newValue) => userName = newValue)

    textField.setOnAction((_: ActionEvent) => {
      if (userName != name && userName != "Common room") {
        meetingManager ! PrivateChatMsg(name, userName, textField.getText)
        meetingManager ! SelfPrivateChatMsg(name, userName, textField.getText)
        textField.setText(null)
      }
      if (userName == "Common room") {
        meetingManager ! CommonChatMsg(name, textField.getText)
        textField.setText(null)
      }
    })


    grid.add(usersListView, 0, 3)

    textArea.setPrefColumnCount(25)
    textArea.setPrefRowCount(5)
    textArea.setEditable(false)

    grid.add(textArea, 1, 3)

    grid.add(textField, 1, 4)
    primaryStage.getIcons.add(new Image("icon.png"))
    primaryStage.setTitle("Chat")
    val scene = new Scene(grid, 740, 510)
    primaryStage.setScene(scene)
    primaryStage.show()
  }

}
