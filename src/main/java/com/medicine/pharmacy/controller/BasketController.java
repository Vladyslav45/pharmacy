package com.medicine.pharmacy.controller;

import com.medicine.pharmacy.config.JavaSenderMail;
import com.medicine.pharmacy.model.Basket;
import com.medicine.pharmacy.model.User;
import com.medicine.pharmacy.service.BasketService;
import com.medicine.pharmacy.service.UserService;
import com.medicine.pharmacy.config.PdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BasketController {

    @Autowired
    private BasketService basketService;
    @Autowired
    private UserService userService;
    @Autowired
    private JavaSenderMail buyEmailSender;
    @Autowired
    private PdfGenerator pdfGenerator;


    @GetMapping(value = "/basket")
    public ModelAndView showBasket(ModelAndView modelAndView, Principal principal) {
        User user = getUser(principal);
        List<Basket> list = basketService.findAllByUserId(user.getId());
        double suma = list.stream().mapToDouble(Basket::getPrice).sum();
        modelAndView.addObject("basketList", list);
        modelAndView.addObject("allprice", suma);
        modelAndView.setViewName("basket/basket");

        return modelAndView;
    }

    @GetMapping(value = "/basket/delete/{id}")
    public ModelAndView deleteItemInBasket(@PathVariable("id") Long id, ModelAndView modelAndView) {
        basketService.deleteItemFromBasket(id);
        modelAndView.setViewName("redirect:/basket");
        return modelAndView;
    }

    @GetMapping(value = "/basket/buy")
    public ModelAndView buy(ModelAndView modelAndView, Principal principal) throws Exception {
        User user = getUser(principal);
        List<Basket> basketList = basketService.findAllByUserId(user.getId());
        ByteArrayInputStream bis = pdfGenerator.generatePdf(basketList, user);
        buyEmailSender.sendWithAttachment(user.getEmail(), bis);
        basketService.delete(user.getId());
        modelAndView.setViewName("redirect:/basket");
        return modelAndView;
    }

    private User getUser(Principal principal) {
        return userService.findById(Long.parseLong(userService.findUserByEmail(principal.getName()).getId().toString()));
    }
}
