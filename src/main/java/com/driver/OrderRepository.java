package com.driver;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class OrderRepository {
    HashMap<String,Order> orderDb;
    HashMap<String,DeliveryPartner> deliveryPartnerDb;
    HashMap<String, HashSet<String>> partnerOrderDb;
    HashMap<String,String> orderToPartner; // assigned Orders

    public OrderRepository() {
        this.orderDb = new HashMap<>();
        this.deliveryPartnerDb = new HashMap<>();
        this.partnerOrderDb = new HashMap<>();
        this.orderToPartner = new HashMap<>();
    }

    public void addOrder(Order order){
        String key = order.getId();
        orderDb.put(key,order);
    }

    public void addPartner(String partnerId){
        DeliveryPartner dp = new DeliveryPartner(partnerId);
        deliveryPartnerDb.put(partnerId,dp);
    }

    public void addOrderPartnerPair(String orderId,String partnerId){
        if(orderDb.containsKey(orderId) && deliveryPartnerDb.containsKey(partnerId)){
            orderToPartner.put(orderId,partnerId);
            HashSet<String> set;
            if(partnerOrderDb.containsKey(partnerId)) set =  partnerOrderDb.get(partnerId);
            else set = new HashSet<>();
            set.add(orderId);
            int orders = deliveryPartnerDb.get(partnerId).getNumberOfOrders();
            deliveryPartnerDb.get(partnerId).setNumberOfOrders(orders+1);
            partnerOrderDb.put(partnerId,set);
        }
    }

    public Order getOrderById(String orderId){
        if(orderDb.containsKey(orderId)) return orderDb.get(orderId);
        return null;
    }

    public DeliveryPartner getPartnerById(String partnerId){
        if(deliveryPartnerDb.containsKey(partnerId)) return deliveryPartnerDb.get(partnerId);
        return null;
    }

    public Integer getOrderCountByPartnerId(String partnerId){
        Integer ans = 0;
        if(deliveryPartnerDb.containsKey(partnerId)){
            ans = deliveryPartnerDb.get(partnerId).getNumberOfOrders();
        }
        return ans;
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        HashSet<String> list = new HashSet<>();
        if(partnerOrderDb.containsKey(partnerId)) list =  partnerOrderDb.get(partnerId);
        return new ArrayList<>(list);
    }

    public List<String> getAllOrders(){
//        List<String> list = new ArrayList<>();
//        for(String orderId : orderDb.keySet()){
//            list.add(orderId);
//        }
//        return list;
        return new ArrayList<>(orderDb.keySet());
    }

    public Integer getCountOfUnassignedOrders(){
        return orderDb.size() - orderToPartner.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        Integer cnt = 0;
        int givenTime = Integer.parseInt(time.substring(0,2))*60 + Integer.parseInt(time.substring(3));
        if(partnerOrderDb.containsKey(partnerId)){
            HashSet<String> set = partnerOrderDb.get(partnerId);
            for(String orderName : set){
                if(orderDb.containsKey(orderName)){
                    int deliveryTime = orderDb.get(orderName).getDeliveryTime();
                    if(givenTime < deliveryTime) cnt++;
                }
            }
        }
        return cnt;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        Integer time = 0;
        if(partnerOrderDb.containsKey(partnerId)){
            for(String order : partnerOrderDb.get(partnerId)){
                if(orderDb.containsKey(order)){
                    time = Math.max(time,orderDb.get(order).getDeliveryTime());
                }
            }
        }
        Integer hrs = time/60;
        Integer minutes = time%60;
        String ans = hrs+":"+minutes;

        return ans;
    }
    public void deletePartnerById(String partnerId){
        if(partnerOrderDb.containsKey(partnerId)){
            for(String order : partnerOrderDb.get(partnerId)){
                orderToPartner.remove(order);
            }
        }
        partnerOrderDb.remove(partnerId);
    }
    public void deleteOrderById(String orderId){
        if(orderDb.containsKey(orderId)){
            for(String partner : partnerOrderDb.keySet()){
                if(partnerOrderDb.get(partner).contains(orderId)){
                    partnerOrderDb.get(partner).remove(orderId);
                }
            }
        }
        orderDb.remove(orderId);
    }
}
