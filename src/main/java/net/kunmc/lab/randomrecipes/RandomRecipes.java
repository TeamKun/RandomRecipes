package net.kunmc.lab.randomrecipes;

import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.codehaus.plexus.util.ReflectionUtils;
import org.bukkit.craftbukkit.v1_16_R3.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RandomRecipes extends JavaPlugin {
    @Override
    public void onEnable() {
        List<Recipe> recipeList = new ArrayList<>();
        Bukkit.recipeIterator().forEachRemaining(recipeList::add);

        List<ItemStack> resultList = new ArrayList<>();
        recipeList.forEach(r -> {
            resultList.add(r.getResult());
        });
        Collections.shuffle(resultList);

        List<Recipe> shuffledRecipeList = new ArrayList<>();
        for (int i = 0; i < recipeList.size(); i++) {
            shuffledRecipeList.add(modifyResult(recipeList.get(i), resultList.get(i)));
        }

        Bukkit.clearRecipes();
        shuffledRecipeList.forEach(Bukkit::addRecipe);
    }

    @Override
    public void onDisable() {
        Bukkit.resetRecipes();
    }

    public Recipe modifyResult(Recipe recipe, ItemStack newResult) {
        if (newResult.getType() == Material.AIR) {
            return recipe;
        }

        if (recipe instanceof CraftSmithingRecipe) {
            CraftSmithingRecipe r = ((CraftSmithingRecipe) recipe);
            return new CraftSmithingRecipe(r.getKey(), newResult, r.getBase(), r.getAddition());
        }

        if (recipe instanceof CraftStonecuttingRecipe) {
            CraftStonecuttingRecipe r = ((CraftStonecuttingRecipe) recipe);
            return new CraftStonecuttingRecipe(r.getKey(), newResult, r.getInputChoice());
        }

        if (recipe instanceof CraftFurnaceRecipe) {
            CraftFurnaceRecipe r = ((CraftFurnaceRecipe) recipe);
            return new CraftFurnaceRecipe(r.getKey(), newResult, r.getInputChoice(), r.getExperience(), r.getCookingTime());
        }

        if (recipe instanceof CraftBlastingRecipe) {
            CraftBlastingRecipe r = ((CraftBlastingRecipe) recipe);
            return new CraftBlastingRecipe(r.getKey(), newResult, r.getInputChoice(), r.getExperience(), r.getCookingTime());
        }

        if (recipe instanceof CraftCampfireRecipe) {
            CraftCampfireRecipe r = ((CraftCampfireRecipe) recipe);
            return new CraftCampfireRecipe(r.getKey(), newResult, r.getInputChoice(), r.getExperience(), r.getCookingTime());
        }

        if (recipe instanceof CraftSmokingRecipe) {
            CraftSmokingRecipe r = ((CraftSmokingRecipe) recipe);
            return new CraftSmokingRecipe(r.getKey(), newResult, r.getInputChoice(), r.getExperience(), r.getCookingTime());
        }

        if (recipe instanceof CraftShapedRecipe) {
            CraftShapedRecipe oldRecipe = ((CraftShapedRecipe) recipe);
            CraftShapedRecipe newRecipe = new CraftShapedRecipe(oldRecipe.getKey(), newResult);

            newRecipe.shape(oldRecipe.getShape());

            Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses("ingredients", ShapedRecipe.class);
            field.setAccessible(true);
            ReflectionUtil.setFieldValue(field, newRecipe, ReflectionUtil.getFieldValue(field, oldRecipe));

            return newRecipe;
        }

        if (recipe instanceof CraftShapelessRecipe) {
            CraftShapelessRecipe oldRecipe = ((CraftShapelessRecipe) recipe);
            CraftShapelessRecipe newRecipe = new CraftShapelessRecipe(oldRecipe.getKey(), newResult);

            Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses("ingredients", ShapelessRecipe.class);
            field.setAccessible(true);
            ReflectionUtil.setFieldValue(field, newRecipe, ReflectionUtil.getFieldValue(field, oldRecipe));

            return newRecipe;
        }

        return null;
    }
}
