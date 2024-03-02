package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;

    public Integer addUser(User user){

        return userRepository.save(user).getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        User user = userRepository.findById(userId).get();
        int count = 0;
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        for (WebSeries webSeries : webSeriesList) {
            if (webSeries.getAgeLimit() <= user.getAge() && user.getSubscription().getSubscriptionType().ordinal() >= webSeries.getSubscriptionType().ordinal()) {
                count++;
            }
        }
        return count;
    }
}

