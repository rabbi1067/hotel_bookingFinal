package bd.hotel_booking.guest;

import bd.hotel_booking.food.*;
import bd.hotel_booking.food.dto.PlaceFoodOrderRequest;
import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class GuestFoodController {

    private final FoodItemRepository foodItemRepository;
    private final FoodOrderService foodOrderService;
    private final UserService userService;
    private final CartService cartService;

    private User currentUser(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }

    @GetMapping("/food-services")
    public String foodServices(Model model) {
        model.addAttribute("foodForJs", FoodJsonMapper.toFrontendList(foodItemRepository.findAll()));
        return "guest/food-services";
    }

    @GetMapping("/food-cart")
    public String foodCart(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        model.addAttribute("foodForJs", FoodJsonMapper.toFrontendList(foodItemRepository.findAll()));
        model.addAttribute("ordersForJs", FoodOrderJsonMapper.toFrontendList(foodOrderService.findForUser(user)));
        model.addAttribute("cartForJs", toCartJson(cartService.getCart(user)));
        return "guest/food-cart";
    }

    @GetMapping("/food-cart/api")
    @ResponseBody
    public List<Map<String, Object>> getCartApi(Authentication authentication) {
        User user = currentUser(authentication);
        return toCartJson(cartService.getCart(user));
    }

    @PostMapping("/food-cart/add")
    @ResponseBody
    public List<Map<String, Object>> addToCart(@RequestParam Long foodItemId,
                                               @RequestParam int delta,
                                               Authentication authentication) {
        User user = currentUser(authentication);
        return toCartJson(cartService.addOrIncrement(user, foodItemId, delta));
    }

    @PostMapping("/food-cart/remove")
    @ResponseBody
    public List<Map<String, Object>> removeFromCart(@RequestParam Long foodItemId,
                                                    Authentication authentication) {
        User user = currentUser(authentication);
        cartService.removeItem(user, foodItemId);
        return toCartJson(cartService.getCart(user));
    }
    @PostMapping("/food-cart/place-order")
    public String placeFoodOrder(@ModelAttribute PlaceFoodOrderRequest request,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        User user = currentUser(authentication);

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(request.paymentMethod().toUpperCase());
        } catch (Exception e) {
            method = PaymentMethod.CASH;
        }

        FoodOrderService.PlaceOrderResult result = foodOrderService.placeOrder(
                user,
                request.cart(),
                request.deliverTo(),
                method,
                request.cardNumber(),
                request.bkashNumber(),
                request.note()
        );

        redirectAttributes.addFlashAttribute("orderOk", result.ok);
        redirectAttributes.addFlashAttribute("orderMsg", result.message);
        if (result.ok) {
            redirectAttributes.addFlashAttribute("orderWhere", result.order.getDeliverTo());
            cartService.clearCart(user);
        }
        return "redirect:/user/food-cart";
    }

    private List<Map<String, Object>> toCartJson(List<CartItem> items) {
        return items.stream().map(ci -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", String.valueOf(ci.getFoodItem().getId()));
            m.put("qty", ci.getQty());
            m.put("name", ci.getFoodItem().getName());
            m.put("price", ci.getFoodItem().getPrice());
            m.put("img", ci.getFoodItem().getImage());
            m.put("cat", ci.getFoodItem().getCategory().name());
            return m;
        }).collect(Collectors.toList());
    }
    @PostMapping("/food-cart/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        User user = currentUser(authentication);
        boolean ok = foodOrderService.cancelByGuest(user, id);
        redirectAttributes.addFlashAttribute("orderMsg", ok ? "Order cancelled." : "This order can no longer be cancelled.");
        redirectAttributes.addFlashAttribute("orderOk", ok);
        return "redirect:/user/food-cart";
    }
}