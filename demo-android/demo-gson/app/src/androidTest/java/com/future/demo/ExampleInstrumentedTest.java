package com.future.demo;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.future.demo", appContext.getPackageName());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("errorCode", 90000);
        jsonObject.addProperty("errorMessage", "错误信息");
        jsonObject.add("data", JsonNull.INSTANCE);

        // region JsonObject 转换为字符串

        // 不保留字段为 null
        Gson gson = new Gson();
        String json = gson.toJson(jsonObject);
        Assert.assertEquals("{\"errorCode\":90000,\"errorMessage\":\"错误信息\"}", json);

        gson = new GsonBuilder()
                // 保留字段为 null
                .serializeNulls()
                .create();
        json = gson.toJson(jsonObject);
        Assert.assertEquals("{\"errorCode\":90000,\"errorMessage\":\"错误信息\",\"data\":null}", json);

        // endregion

        // region 字符串转换为 JsonElement

        // 使用 JsonParser 转换
        JsonElement jsonElement = JsonParser.parseString(json);
        Assert.assertTrue(jsonElement instanceof JsonObject);
        Assert.assertEquals(JsonNull.INSTANCE, ((JsonObject) jsonElement).get("data").getAsJsonNull());
        Assert.assertEquals(90000, ((JsonObject) jsonElement).get("errorCode").getAsInt());
        Assert.assertEquals("错误信息", ((JsonObject) jsonElement).get("errorMessage").getAsString());

        // 使用 Gson.fromJson 方法转换
        jsonElement = gson.fromJson(json, JsonElement.class);
        Assert.assertTrue(jsonElement instanceof JsonObject);
        Assert.assertEquals(JsonNull.INSTANCE, ((JsonObject) jsonElement).get("data").getAsJsonNull());
        Assert.assertEquals(90000, ((JsonObject) jsonElement).get("errorCode").getAsInt());
        Assert.assertEquals("错误信息", ((JsonObject) jsonElement).get("errorMessage").getAsString());

        // endregion
    }
}