package bb.Controller;

import bb.Model.ModifyModel;
import bb.View.ModifyView;
import com.mpatric.mp3agic.NotSupportedException;

import java.io.IOException;

public class ModifyController {

    private ModifyModel model;
    private ModifyView view;

    public ModifyController(ModifyModel model, boolean isMultipleFiles) {
        this.model = model;
        view = new ModifyView(this, isMultipleFiles);
    }

    public void clearSelectedFiles() {
        model.clearSelectedFiles();
    }
    public String getArtistName() {
        return model.getArtistName();
    }
    public String getOriginalFileName() { return model.getOriginalFileName(); }

    public String excludeString(String excludeFrom, String excludeThat) {
        return model.excludeString(excludeFrom, excludeThat);
    }
    public String excludeString(String excludeThat) {
        return model.excludeString(excludeThat);
    }

    public String[] getGenres() {
        return model.getGenres();
    }

    public Object[][] getTableData() {
        return model.getTableData();
    }

    public void submit(String[] submitData, boolean use3v23) throws IOException, NotSupportedException {
        model.submit(submitData,use3v23);
    }
    public void submit (Object[][] submitData, boolean use3v23) throws IOException, NotSupportedException {
        model.submit(submitData,use3v23);
    }
}
