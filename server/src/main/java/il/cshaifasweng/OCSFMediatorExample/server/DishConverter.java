package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;

import java.util.List;
import java.util.stream.Collectors;

public class DishConverter {

    public static List<DishEnt> convertToDishEntList(List<Dish> dishes) {
        return dishes.stream().map(dish -> new DishEnt(
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getIngredients(),
                dish.getPrice()
        )).collect(Collectors.toList());
    }


}