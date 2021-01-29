import CommonWindow._
import Main._
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.Scene
import javafx.scene.control.{TextArea, TextField}
import javafx.scene.image.Image
import javafx.scene.layout.VBox
import javafx.stage.{Modality, Stage}
class CommonWindow {
  def  newWindow: Unit = {
    val primaryStage = new Stage
    primaryStage.initModality(Modality.APPLICATION_MODAL)
    root.setPrefSize(500,500)

    textArea.setPrefHeight(450)
    textArea.setEditable(false)

    textField.setOnAction(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        meetingManager ! CommonChatMsg(name, textField.getText)
        textField.setText(null)
      }
    })
    primaryStage.centerOnScreen()
    primaryStage.getIcons.add(new Image("icon.png"))
    primaryStage.setTitle("Free Chat")

    primaryStage.setScene(new Scene(root))
    primaryStage.show()
  }
}
object CommonWindow{
  val textArea = new TextArea()
  val textField = new TextField()
  val root =new VBox(10,textArea,textField)
}
