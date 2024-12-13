package sheridan.gcaa.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public interface IDataPacketGen {
    Gson GSON = new Gson();
    void writeData(JsonObject jsonObject);
    void loadData(JsonObject jsonObject);
}
