package bd.hotel_booking.admin;

import bd.hotel_booking.food.*;
import bd.hotel_booking.food.cloudinary.CloudinaryService;
import bd.hotel_booking.food.dto.FoodItemFormRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminFoodController {

    private final FoodItemService foodItemService;
    private final FoodOrderService foodOrderService;
    private final CloudinaryService cloudinaryService;

    @GetMapping("/food-management")
    public String foodManagement(Model model) {
        model.addAttribute("foodForJs", FoodJsonMapper.toFrontendList(foodItemService.findAll()));
        return "admin/food-management";
    }

    @PostMapping("/food-management/save")
    public String saveFoodItem(@ModelAttribute FoodItemFormRequest request,
                               @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                               RedirectAttributes redirectAttributes) throws IOException {
        FoodCategory category;
        try {
            category = FoodCategory.valueOf(request.cat().toUpperCase());
        } catch (IllegalArgumentException e) {
            category = FoodCategory.SNACKS;
        }
        boolean available = request.available() == null || request.available();
        String imageUrl = request.img();
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = cloudinaryService.upload(imageFile);
        }
        if (imageUrl == null || imageUrl.isBlank()) {
            redirectAttributes.addFlashAttribute("dishError", "Please upload a dish image before saving.");
            return "redirect:/admin/food-management";
        }
        foodItemService.save(request.id(), request.name(), request.desc(), request.price(),
                category, imageUrl, available);
        redirectAttributes.addFlashAttribute("dishSuccess", "Dish saved successfully!");
        return "redirect:/admin/food-management";
    }

    @PostMapping("/food-management/{id}/toggle")
    public String toggleFoodAvailability(@PathVariable Long id,
                                         RedirectAttributes redirectAttributes) {
        foodItemService.toggleAvailability(id);
        redirectAttributes.addFlashAttribute("dishSuccess", "Dish availability updated successfully!");
        return "redirect:/admin/food-management";
    }

    @PostMapping("/food-management/{id}/delete")
    public String deleteFoodItem(@PathVariable Long id) {
        foodItemService.deleteById(id);
        return "redirect:/admin/food-management";
    }

    @GetMapping("/food-orders")
    public String foodOrders(Model model) {
        model.addAttribute("ordersForJs",
                FoodOrderJsonMapper.toFrontendList(foodOrderService.findAll()));
        return "admin/food-orders";
    }

    @PostMapping("/food-orders/{id}/status")
    public String updateFoodOrderStatus(@PathVariable Long id,
                                        @RequestParam String status,
                                        @RequestParam(required = false) String reason) {
        FoodOrderStatus parsed;
        try {
            parsed = FoodOrderStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            parsed = FoodOrderStatus.PLACED;
        }
        foodOrderService.updateStatus(id, parsed, reason);
        return "redirect:/admin/food-orders";
    }

    @PostMapping("/food-orders/bulk-status")
    public String bulkUpdateStatus(@RequestParam("orderIds") List<Long> orderIds,
                                   @RequestParam String status) {
        FoodOrderStatus parsed;
        try {
            parsed = FoodOrderStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return "redirect:/admin/food-orders";
        }
        for (Long id : orderIds) {
            foodOrderService.updateStatus(id, parsed, null);
        }
        return "redirect:/admin/food-orders";
    }
}
