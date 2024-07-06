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
import model.ChatModel.commonRoom

class CommonWindow(name: String, meetingManager: ActorRef, model: ChatModel) {

  def start(): Unit = {
    val textArea = new TextArea()
    val textField = new TextField()
    val usersListView = new ListView[String](model.users)

    model
      .newMessage
      .addListener((_, _, newValue) => textArea.appendText(newValue))

    val primaryStage = new Stage()
    val grid = new GridPane
    grid.setAlignment(Pos.CENTER)
    grid.setHgap(10)
    grid.setVgap(10)
    grid.setPadding(new Insets(25, 25, 25, 25))

    usersListView.getSelectionModel.selectedItemProperty

    textField.setOnAction { (_: ActionEvent) =>
      val selectedUserName = usersListView.getSelectionModel().getSelectedItem()
      sendMessage(selectedUserName, textField.getText())
      textField.setText(null)
    }

    grid.add(usersListView, 0, 3)

    textArea.setPrefColumnCount(25)
    textArea.setPrefRowCount(5)
    textArea.setEditable(false)

    grid.add(textArea, 1, 3)

    grid.add(textField, 1, 4)
    primaryStage.getIcons.add(new Image("icon.png"))
    primaryStage.setTitle(name)
    val scene = new Scene(grid, 740, 510)
    primaryStage.setScene(scene)
    primaryStage.show()
  }

  private def sendMessage(destination: String, msg: String): Unit = {
    destination match {
      case _ if destination != name && destination != commonRoom =>
        meetingManager ! PrivateChatMsg(
          from = name,
          to = destination,
          message = msg
        )
        meetingManager ! SelfPrivateChatMsg(
          from = name,
          to = destination,
          message = msg
        )
      case _ if destination == commonRoom =>
        meetingManager ! CommonChatMsg(from = name, message = msg)
    }
  }

}
