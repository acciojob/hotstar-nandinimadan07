package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service

public class SubscriptionService {

    @Autowired SubscriptionRepository subscriptionRepository;

    @Autowired UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){
//Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription(); subscription.setUser(user);
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());

        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date()); int subscriptionCost = 0;
        switch(subscription.getSubscriptionType()){
            case BASIC: subscriptionCost = 500 + 200 * subscriptionEntryDto.getNoOfScreensRequired();
                break;
            case PRO: subscriptionCost = 800 + 250 * subscriptionEntryDto.getNoOfScreensRequired();
                break;
            case ELITE: subscriptionCost = 1000 + 350 * subscriptionEntryDto.getNoOfScreensRequired();
                break;
        }
        subscription.setTotalAmountPaid(subscriptionCost);
        subscriptionRepository.save(subscription);
        return subscriptionCost;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{
//If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
//In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
//update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription currentSubscription = user.getSubscription();
        if(currentSubscription.getSubscriptionType() == SubscriptionType.ELITE)
        {
            throw new Exception("Already the best Subscription");
        }
        Subscription newSubscription;
        int upgradeCost = 0;
        if(currentSubscription.getSubscriptionType() == SubscriptionType.BASIC){
            newSubscription = new Subscription();
            newSubscription.setUser(user);
            newSubscription.setSubscriptionType(SubscriptionType.PRO);
            newSubscription.setNoOfScreensSubscribed(currentSubscription.getNoOfScreensSubscribed());
            newSubscription.setStartSubscriptionDate(new Date()); int newSubscriptionCost = 0;
            newSubscriptionCost = 800 + 250 * newSubscription.getNoOfScreensSubscribed();
            upgradeCost = newSubscriptionCost - currentSubscription.getTotalAmountPaid();
        }else
        {
            newSubscription = new Subscription();
            newSubscription.setUser(user);
            newSubscription.setSubscriptionType(SubscriptionType.ELITE);
            newSubscription.setNoOfScreensSubscribed(currentSubscription.getNoOfScreensSubscribed());
            newSubscription.setStartSubscriptionDate(new Date());
            int newSubscriptionCost = 1000 + 350 * newSubscription.getNoOfScreensSubscribed();
            upgradeCost = newSubscriptionCost - currentSubscription.getTotalAmountPaid();
        }
        subscriptionRepository.delete(currentSubscription);
        newSubscription.setTotalAmountPaid(currentSubscription.getTotalAmountPaid() + upgradeCost);
        subscriptionRepository.save(newSubscription);
        return upgradeCost;
    }

    public Integer calculateTotalRevenueOfHotstar(){
//We need to find out total Revenue of hotstar : from all the subscriptions combined
//Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int totalRevenue = 0;
        for(Subscription subscription: subscriptions){
            totalRevenue += subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
