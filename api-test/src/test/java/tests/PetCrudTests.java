package tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import model.dto.Pet;
import utils.PetHelper;

@Epic("Petstore API Tests")
@Feature("Pet CRUD Operations")
public class PetCrudTests extends BaseTest {

    private Integer petId;
    private static final Faker faker = new Faker();

    /**
     * Setup method for tests that need an existing pet
     * Creates a pet and stores its ID for use in the test
     */
    @BeforeMethod(onlyForGroups = {"update", "delete", "get"})
    public void createPetForTest() {
        logTestStart("Setup - Creating pet for test");
        Pet pet = PetHelper.createSimplePet();
        petId = pet.getId(); // Store the ID we're creating with, not from response
        Response response = PetHelper.createPet(pet);
        Assert.assertEquals(response.getStatusCode(), 200);
        
        // Get the actual created pet ID from response if available
        try {
            Pet createdPet = response.as(Pet.class);
            if (createdPet.getId() != null) {
                petId = createdPet.getId();
            }
        } catch (Exception e) {
            // If we can't parse response, keep the original petId
        }
        
        logTestEnd("Setup - Pet created with ID: " + petId);
    }

    /**
     * Cleanup method to delete pet after specific tests
     * Runs for tests that create pets and need cleanup
     */
    @AfterMethod(onlyForGroups = {"create", "negative-update", "update", "get"})
    public void cleanupPet() {
        if (petId != null) {
            logTestStart("Cleanup - Deleting pet with ID: " + petId);
            // Clean up the pet, but don't fail the test if deletion fails
            try {
                PetHelper.deletePet(petId);
                logTestEnd("Cleanup - Pet deleted successfully");
            } catch (Exception e) {
                logTestEnd("Cleanup - Pet deletion failed (may already be deleted): " + e.getMessage());
            }
            petId = null;
        }
    }

    @Test(groups = "create", description = "Create a new pet with realistic data")
    @Story("Create Pet")
    public void testCreatePet() {
        logTestStart("testCreatePet");

        // Use helper method to create a simple pet with realistic data
        Pet pet = PetHelper.createSimplePet();

        Response response = PetHelper.createPet(pet);
        Assert.assertEquals(response.getStatusCode(), 200);

        Pet createdPet = response.as(Pet.class);
        Assert.assertNotNull(createdPet.getId());
        Assert.assertEquals(createdPet.getName(), pet.getName());
        Assert.assertEquals(createdPet.getStatus(), pet.getStatus());

        // Store the ID for cleanup in @AfterMethod
        petId = createdPet.getId();

        logTestEnd("testCreatePet");
    }

    @Test(groups = "get", description = "Get pet by ID")
    @Story("Get Pet")
    public void testGetPetById() {
        logTestStart("testGetPetById");

        Response response = PetHelper.getPetById(petId);
        Assert.assertEquals(response.getStatusCode(), 200);

        Pet fetchedPet = response.as(Pet.class);
        Assert.assertEquals(fetchedPet.getId(), petId);
        Assert.assertNotNull(fetchedPet.getName());
        Assert.assertEquals(fetchedPet.getStatus(), Pet.Status.AVAILABLE);

        logTestEnd("testGetPetById");
    }

    @Test(groups = "update", description = "Update pet details with realistic data")
    @Story("Update Pet")
    public void testUpdatePet() {
        logTestStart("testUpdatePet");

        // Create a detailed pet with full information using realistic data
        Pet updatedPet = PetHelper.createDetailedPet();
        updatedPet.setId(petId); // Use the pet ID created in @BeforeMethod
        updatedPet.setStatus(Pet.Status.SOLD);

        Response response = PetHelper.updatePet(updatedPet);
        Assert.assertEquals(response.getStatusCode(), 200);

        Pet fetchedPet = response.as(Pet.class);
        Assert.assertNotNull(fetchedPet.getName());
        Assert.assertEquals(fetchedPet.getStatus(), Pet.Status.SOLD);
        Assert.assertNotNull(fetchedPet.getCategory());
        Assert.assertNotNull(fetchedPet.getCategory().getName());
        Assert.assertNotNull(fetchedPet.getTags());
        Assert.assertFalse(fetchedPet.getTags().isEmpty());

        logTestEnd("testUpdatePet");
    }

    @Test(groups = "delete", description = "Delete pet by ID")
    @Story("Delete Pet")
    public void testDeletePet() {
        logTestStart("testDeletePet");

        Response response = PetHelper.deletePet(petId);
        Assert.assertEquals(response.getStatusCode(), 200);

        // Verify pet is deleted by trying to get it
        try {
            Response verifyResponse = PetHelper.getPetById(petId);
            Assert.assertEquals(verifyResponse.getStatusCode(), 404);
        } catch (Exception e) {
            // RestAssured throws exception for 404, which is expected
            Assert.assertTrue(e.getMessage().contains("404"));
        }

        // Set petId to null since we've successfully deleted it
        petId = null;

        logTestEnd("testDeletePet");
    }

    // ===============================
    // NEGATIVE TEST SCENARIOS
    // ===============================

    @Test(description = "Create pet with invalid body - should return 400")
    @Story("Create Pet - Negative")
    public void testCreatePetWithInvalidBody() {
        logTestStart("testCreatePetWithInvalidBody");

        // Generate random invalid JSON string
        String invalidJson = faker.lorem().word() + "-" + faker.number().digits(5) + "-invalid";
        Response response = PetHelper.createPetWithInvalidBody(invalidJson);
        Assert.assertEquals(response.getStatusCode(), 400);

        logTestEnd("testCreatePetWithInvalidBody");
    }

    @Test(description = "Get non-existing pet - should return 404")
    @Story("Get Pet - Negative")
    public void testGetNonExistingPet() {
        logTestStart("testGetNonExistingPet");

        // Generate a random high number that's unlikely to exist
        Integer nonExistentPetId = faker.number().numberBetween(900000000, 999999999);
        try {
            Response response = PetHelper.getPetById(nonExistentPetId);
            Assert.assertEquals(response.getStatusCode(), 404);
        } catch (Exception e) {
            // RestAssured throws exception for 404, which is expected
            Assert.assertTrue(e.getMessage().contains("404"));
        }

        logTestEnd("testGetNonExistingPet");
    }

    @Test(groups = "negative-update", description = "Update non-existing pet with realistic data - should return 404 or create new pet")
    @Story("Update Pet - Negative")
    public void testUpdateNonExistingPet() {
        logTestStart("testUpdateNonExistingPet");

        Pet nonExistentPet = PetHelper.createSimplePet();
        // Generate a random high number that's unlikely to exist
        Integer randomNonExistentId = faker.number().numberBetween(900000000, 999999999);
        nonExistentPet.setId(randomNonExistentId);
        petId = nonExistentPet.getId(); // Store the ID we're sending for potential cleanup

        Response response = PetHelper.updatePet(nonExistentPet);
        // Note: Swagger Petstore demo API creates a new pet instead of returning 404
        // This is documented behavior for demonstration purposes
        Assert.assertTrue(response.getStatusCode() == 404 || response.getStatusCode() == 200,
                "Expected 404 (not found) or 200 (if API creates new pet)");

        // If response is 404, no pet was created, so no cleanup needed
        if (response.getStatusCode() == 404) {
            petId = null;
        }

        logTestEnd("testUpdateNonExistingPet");
    }    @Test(description = "Delete non-existing pet - should return 404")
    @Story("Delete Pet - Negative")
    public void testDeleteNonExistingPet() {
        logTestStart("testDeleteNonExistingPet");

        // Generate a random high number that's unlikely to exist
        Integer nonExistentPetId = faker.number().numberBetween(900000000, 999999999);
        try {
            Response response = PetHelper.deletePet(nonExistentPetId);
            Assert.assertEquals(response.getStatusCode(), 404);
        } catch (Exception e) {
            // RestAssured throws exception for 404, which is expected
            Assert.assertTrue(e.getMessage().contains("404"));
        }

        logTestEnd("testDeleteNonExistingPet");
    }
}
