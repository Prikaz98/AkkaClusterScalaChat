import CommonWindow._
import Main._
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.FXCollections
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control.{ListView, TextArea, TextField}
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.stage.Stage
class CommonWindow {
  def  newWindow: Unit = {
    val primaryStage = new Stage

    val grid = new GridPane
    grid.setAlignment(Pos.CENTER)
    grid.setHgap(10)
    grid.setVgap(10)
    grid.setPadding(new Insets(25, 25, 25, 25))

    val usersSelectionModel = CommonWindow.usersListView.getSelectionModel
    usersSelectionModel.selectedItemProperty.addListener(new ChangeListener[String]() {
      override def changed(changed: ObservableValue[_ <: String], oldValue: String, newValue: String) = {
        userValue(0) = newValue
      }
    })

    textField.setOnAction(new EventHandler[ActionEvent] {
      override def handle(event: ActionEvent): Unit = {
        if (userValue(0) != name && userValue(0) != "Common room") {
          meetingManager ! PrivateChatMsg(name, userValue(0), textField.getText)
          meetingManager ! SelfPrivateChatMsg(name, userValue(0), textField.getText)
          textField.setText(null)
        }
        if (userValue(0) == "Common room") {
          meetingManager ! CommonChatMsg(name, textField.getText)
          textField.setText(null)
        }
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
object CommonWindow{
  val textArea = new TextArea()
  val textField = new TextField()
  val userValue = Array(name)
  val users = FXCollections.observableArrayList("Common room", name)
  val usersListView = new ListView[String](users)}
