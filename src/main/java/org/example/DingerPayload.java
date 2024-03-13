package org.example;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DingerPayload {

    private String clientId;
    private String publicKey;
    private String items;
    private String customerName;
    private int totalAmount;
    private String merchantOrderId;
    private String merchantKey;
    private String projectName;
    private String merchantName;

    @Getter
    @Setter
    public static class Item {
        private String name;
        private int amount;
        private int quantity;
    }

    public static DingerPayload create(){
        List<DingerPayload.Item> items = new ArrayList<>();
        DingerPayload.Item item = new DingerPayload.Item();
        item.setName("Dinger");
        item.setAmount(1100);
        item.setQuantity(2);
        items.add(item);

        // Creating PaymentData object
        DingerPayload paymentData = new DingerPayload();
        paymentData.setClientId(DingerConstant.clientId);
        paymentData.setPublicKey(DingerConstant.publicKey);
        paymentData.setItems(convertObjectToJsonString(items));
        paymentData.setCustomerName("Kyaw Kyaw");
        paymentData.setTotalAmount(2200);
        paymentData.setMerchantOrderId("yourMerchantOrderId");
        paymentData.setMerchantKey(DingerConstant.merchantKey);
        paymentData.setProjectName(DingerConstant.projectName);
        paymentData.setMerchantName(DingerConstant.merchantName);

        return paymentData;
    }

    private static String convertObjectToJsonString(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

}