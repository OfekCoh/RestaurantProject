package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.DishEnt;

import java.util.List;
import java.util.stream.Collectors;

public class DishConverter {

    public static List<DishEnt> convertToDishEntList(List<Dish> dishes) {
        return dishes.stream()
                .map(dish -> {
                    // Pull out the List<String> first
                    List<String> ingredients = dish.getIngredients();
                    // Convert it to String[]
                    String[] ingredientsArr = ingredients.toArray(new String[ingredients.size()]);

                    // Now create the DishEnt
                    return new DishEnt(
                            dish.getId(),
                            dish.getName(),
                            dish.getDescription(),
                            ingredientsArr,
                            dish.getPrice()
                    );
                })
                .collect(Collectors.toList());
    }


}