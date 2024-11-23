package Interface;

import model.Note;
import model.Task;

public interface iClickItemTask {
    void onClickItemTask(Task task);

    void deleteData(String noteID);

}
