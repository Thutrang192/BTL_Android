package Interface;

import model.Note;

public interface iClickItemNote {

    // dinh nghia cac ham muon callback ra ben ngoai
    void onClickItemNote(Note note);

    void deleteData(String noteID, Note note);
}
