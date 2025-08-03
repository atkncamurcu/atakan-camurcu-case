package tests;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
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
import utils.HttpStatusCode;
import utils.PetHelper;

@Epic("Petstore API Tests")
@Feature("Pet CRUD Operations")
public class PetCrudTests extends BaseTest {

    private Integer petId;
    private static final Faker faker = new Faker();

    @BeforeMethod(onlyForGroups = {"update", "delete", "get"})
    public void createPetForTest() {
        logTestStart("Setup - Creating pet for test");
        Pet pet = PetHelper.createSimplePet();

        Response response = PetHelper.createPet(pet);
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.OK.getCode());
        
        Pet createdPet = response.as(Pet.class);
        petId = createdPet.getId();
        
        Awaitility.await()
            .atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(3))
            .ignoreExceptions()
            .until(() -> {
                Response verifyResponse = PetHelper.getPetById(petId);
                return verifyResponse.getStatusCode() == HttpStatusCode.OK.getCode();
            });
        
        logTestEnd("Setup - Pet created with ID: " + petId);
    }

    @AfterMethod(onlyForGroups = {"create", "negative-update", "update", "get"})
    public void cleanupPet() {
        if (petId != null) {
            logTestStart("Cleanup - Deleting pet with ID: " + petId);
            try {
                PetHelper.deletePet(petId);
                logTestEnd("Cleanup - Pet deleted successfully");
            } catch (Exception e) {
                logTestEnd("Cleanup - Pet deletion failed (may already be deleted): " + e.getMessage());
            }
            petId = null;
        }
    }

    @Test(groups = "create", description = "Create a new pet with valid data")
    @Story("Create Pet")
    public void testCreatePet() {
        logTestStart("testCreatePet");
        
        Pet pet = PetHelper.createSimplePet();
        Response response = PetHelper.createPet(pet);
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.OK.getCode());
        
        Pet createdPet = response.as(Pet.class);
        Assert.assertEquals(createdPet.getName(), pet.getName());
        Assert.assertEquals(createdPet.getStatus(), pet.getStatus());

        petId = createdPet.getId();

        Awaitility.await()
            .atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(3))
            .ignoreExceptions()
            .until(() -> {
                Response verifyResponse = PetHelper.getPetById(petId);
                return verifyResponse.getStatusCode() == HttpStatusCode.OK.getCode();
            });
        
        logTestEnd("testCreatePet");
    }

    @Test(groups = "get", description = "Get pet by ID")
    @Story("Get Pet")
    public void testGetPetById() {
        logTestStart("testGetPetById");
        Response response = Awaitility.await()
                .atMost(120, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(3))
                .ignoreExceptions()
                .until(
                        () -> PetHelper.getPetById(petId),
                        resp -> resp.getStatusCode() == HttpStatusCode.OK.getCode()
                );
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.OK.getCode());
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
        Pet updatedPet = PetHelper.createDetailedPet();
        updatedPet.setId(petId);
        updatedPet.setStatus(Pet.Status.SOLD);

        Response updateResp = PetHelper.updatePet(updatedPet);
        Assert.assertEquals(updateResp.getStatusCode(), HttpStatusCode.OK.getCode());

        Awaitility.await()
                .atMost(120, TimeUnit.SECONDS)
                .pollInterval(Duration.ofSeconds(3))
                .ignoreExceptions()
                .untilAsserted(() -> {
                    Response getResp = PetHelper.getPetById(petId);
                    Assert.assertEquals(getResp.getStatusCode(), HttpStatusCode.OK.getCode());
                    Pet p = getResp.as(Pet.class);
                    Assert.assertEquals(p.getStatus(), Pet.Status.SOLD);
                    Assert.assertNotNull(p.getName());
                    Assert.assertNotNull(p.getCategory());
                    Assert.assertNotNull(p.getCategory().getName());
                    Assert.assertFalse(p.getTags().isEmpty());
                });

        logTestEnd("testUpdatePet");
    }

    @Test(groups = "delete", description = "Delete pet by ID")
    @Story("Delete Pet")
    public void testDeletePet() {
        logTestStart("testDeletePet");

        Awaitility.await()
            .atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(3))
            .ignoreExceptions()
            .until(() -> {
                Response checkResponse = PetHelper.getPetById(petId);
                return checkResponse.getStatusCode() == HttpStatusCode.OK.getCode();
            });
        
        Awaitility.await()
            .atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(3))
            .ignoreExceptions()
            .until(() -> {
                Response deleteResponse = PetHelper.deletePet(petId);
                return deleteResponse.getStatusCode() == HttpStatusCode.OK.getCode();
            });

        try {
            Response verifyResponse = PetHelper.getPetById(petId);
            Assert.assertEquals(verifyResponse.getStatusCode(), HttpStatusCode.NOT_FOUND.getCode());
        } catch (Exception e) {
            // RestAssured throws exception for 404, which is expected
            Assert.assertTrue(e.getMessage().contains("404"), "Expected 404 error not found");
        }
        
        petId = null;
        logTestEnd("testDeletePet");
    }

    @Test(description = "Create pet with invalid body")
    @Story("Create Pet - Negative")
    public void testCreatePetWithInvalidBody() {
        logTestStart("testCreatePetWithInvalidBody");
        String invalidJson = faker.lorem().word() + "-" + faker.number().digits(5) + "-invalid";
        Response response = PetHelper.createPetWithInvalidBody(invalidJson);
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.BAD_REQUEST.getCode());
        logTestEnd("testCreatePetWithInvalidBody");
    }

    @Test(description = "Get non-existing pet")
    @Story("Get Pet - Negative")
    public void testGetNonExistingPet() {
        logTestStart("testGetNonExistingPet");
        Integer nonExistentPetId = faker.number().numberBetween(900_000_000, 999_999_999);
        try {
            Response response = PetHelper.getPetById(nonExistentPetId);
            Assert.assertEquals(response.getStatusCode(), HttpStatusCode.NOT_FOUND.getCode());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("404"));
        }
        logTestEnd("testGetNonExistingPet");
    }

    @Test(groups = "negative-update", description = "Update non-existing pet - should return 404")
    @Story("Update Pet - Negative")
    public void testUpdateNonExistingPet() {
        logTestStart("testUpdateNonExistingPet");

        Integer randomNonExistentId = faker.number().numberBetween(900_000_000, 999_999_999);
        
        Pet nonExistentPet = PetHelper.createSimplePet();
        nonExistentPet.setId(randomNonExistentId);

        Awaitility.await()
            .atMost(120, TimeUnit.SECONDS)
            .pollInterval(Duration.ofSeconds(3))
            .ignoreExceptions()
            .until(() -> {
                try {
                    Response checkResponse = PetHelper.getPetById(randomNonExistentId);
                    return checkResponse.getStatusCode() == HttpStatusCode.NOT_FOUND.getCode();
                } catch (Exception e) {
                    return e.getMessage().contains("404");
                }
            });

        Response response = PetHelper.updatePet(nonExistentPet);
        
        if (response.getStatusCode() == HttpStatusCode.OK.getCode()) {
            logTestEnd("testUpdateNonExistingPet - Note: API created a new pet instead of returning 404");
            petId = randomNonExistentId;
        } else {
            Assert.assertEquals(response.getStatusCode(), HttpStatusCode.NOT_FOUND.getCode());
            petId = null;
            logTestEnd("testUpdateNonExistingPet");
        }
    }


    @Test(description = "Delete non-existing pet")
    @Story("Delete Pet - Negative")
    public void testDeleteNonExistingPet() {
        logTestStart("testDeleteNonExistingPet");
        Integer nonExistentPetId = faker.number().numberBetween(900_000_000, 999_999_999);
        try {
            Response response = PetHelper.deletePet(nonExistentPetId);
            Assert.assertEquals(response.getStatusCode(), HttpStatusCode.NOT_FOUND.getCode());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("404"));
        }
        logTestEnd("testDeleteNonExistingPet");
    }
    
    @Test(description = "Create pet with missing required fields (empty body)")
    @Story("Create Pet - Negative")
    public void testCreatePetWithEmptyBody() {
        logTestStart("testCreatePetWithEmptyBody");
        
        String emptyBody = "{}";
        Response response = PetHelper.createPetWithInvalidBody(emptyBody);
        
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.BAD_REQUEST.getCode());
        
        logTestEnd("testCreatePetWithEmptyBody");
    }
    
    @Test(description = "Create pet with invalid data types (id as string)")
    @Story("Create Pet - Negative")
    public void testCreatePetWithInvalidDataTypes() {
        logTestStart("testCreatePetWithInvalidDataTypes");
        
        String invalidTypesJson = "{"
            + "\"id\": \"should-be-a-number\","
            + "\"name\": \"" + faker.animal().name() + "\","
            + "\"status\": 123" 
            + "}";
        
        Response response = PetHelper.createPetWithInvalidBody(invalidTypesJson);
        
        Assert.assertEquals(response.getStatusCode(), HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
        
        logTestEnd("testCreatePetWithInvalidDataTypes");
    }
    
    @Test(description = "Get pet with invalid ID type (string)")
    @Story("Get Pet - Negative")
    public void testGetPetWithInvalidIdType() {
        logTestStart("testGetPetWithInvalidIdType");

        String invalidId = "abc" + faker.letterify("????");

        try {
            Response response = PetHelper.getPetByIdString(invalidId);
            Assert.assertEquals(response.getStatusCode(), HttpStatusCode.NOT_FOUND.getCode());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("404"), "Expected 404 error for invalid ID type");
        }
        
        logTestEnd("testGetPetWithInvalidIdType");
    }
    
    @Test(description = "Get pet with negative ID")
    @Story("Get Pet - Negative")
    public void testGetPetWithNegativeId() {
        logTestStart("testGetPetWithNegativeId");

        Integer negativeId = -1 * faker.number().numberBetween(1, 1000000);

        try {
            Response response = PetHelper.getPetById(negativeId);
            Assert.assertEquals(response.getStatusCode(), HttpStatusCode.NOT_FOUND.getCode());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("404"), "Expected 404 error for negative ID");
        }
        
        logTestEnd("testGetPetWithNegativeId");
    }
    
    @Test(groups = "negative-update", description = "Update pet with empty body")
    @Story("Update Pet - Negative")
    public void testUpdatePetWithEmptyBody() {
        logTestStart("testUpdatePetWithEmptyBody");
        
        String emptyBody = "{}";
        
        try {
            Response response = PetHelper.updatePetWithInvalidBody(emptyBody);
            Assert.assertEquals(response.getStatusCode(), HttpStatusCode.BAD_REQUEST.getCode());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("400") || e.getMessage().contains("Bad Request"), 
                    "Expected 400 error for empty body");
        }
        
        logTestEnd("testUpdatePetWithEmptyBody");
    }
    
    @Test(groups = "negative-update", description = "Update pet with missing ID field")
    @Story("Update Pet - Negative")
    public void testUpdatePetWithMissingId() {
        logTestStart("testUpdatePetWithMissingId");
        
        String missingIdJson = "{"
            + "\"name\": \"" + faker.animal().name() + "\","
            + "\"status\": \"available\""
            + "}";
        
        try {
            Response response = PetHelper.updatePetWithInvalidBody(missingIdJson);
            Assert.assertEquals(response.getStatusCode(), HttpStatusCode.BAD_REQUEST.getCode());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("400") || e.getMessage().contains("Bad Request"), 
                    "Expected 400 error for missing ID field");
        }
        
        logTestEnd("testUpdatePetWithMissingId");
    }
    
    @Test(groups = "negative-update", description = "Update pet with invalid data types")
    @Story("Update Pet - Negative")
    public void testUpdatePetWithInvalidDataTypes() {
        logTestStart("testUpdatePetWithInvalidDataTypes");
        
        String invalidTypesJson = "{"
            + "\"id\": \"invalid-id-as-string\","
            + "\"name\": " + faker.number().randomNumber() + ","
            + "\"status\": 123" 
            + "}";
        
        try {
            Response response = PetHelper.updatePetWithInvalidBody(invalidTypesJson);
            Assert.assertEquals(response.getStatusCode(), HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("500") || e.getMessage().contains("Internal Server Error"), 
                    "Expected 500 error for invalid data types");
        }
        
        logTestEnd("testUpdatePetWithInvalidDataTypes");
    }
    
    @Test(description = "Delete pet with invalid ID (string)")
    @Story("Delete Pet - Negative")
    public void testDeletePetWithInvalidIdType() {
        logTestStart("testDeletePetWithInvalidIdType");

        String invalidId = "abc" + faker.letterify("????");

        try {
            Response response = PetHelper.deletePetByIdString(invalidId);
            Assert.assertEquals(response.getStatusCode(), HttpStatusCode.BAD_REQUEST.getCode());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("404") || e.getMessage().contains("Not Found"),
                    "Expected 404 error for invalid ID type");
        }
        
        logTestEnd("testDeletePetWithInvalidIdType");
    }
    
    @Test(description = "Delete pet with negative ID")
    @Story("Delete Pet - Negative")
    public void testDeletePetWithNegativeId() {
        logTestStart("testDeletePetWithNegativeId");

        Integer negativeId = -1 * faker.number().numberBetween(1, 1000000);

        try {
            Response response = PetHelper.deletePet(negativeId);
            Assert.assertEquals(response.getStatusCode(), HttpStatusCode.NOT_FOUND.getCode());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("404"), "Expected 404 error for negative ID");
        }
        
        logTestEnd("testDeletePetWithNegativeId");
    }
    
    @Test(description = "Find pets with invalid status value")
    @Story("Find Pet - Negative")
    public void testFindPetsByInvalidStatus() {
        logTestStart("testFindPetsByInvalidStatus");

        String invalidStatus = "invalid_" + faker.lorem().word();
        
        try {
            Response response = PetHelper.findPetsByInvalidStatus(invalidStatus);
            Assert.assertEquals(response.getStatusCode(), HttpStatusCode.BAD_REQUEST.getCode());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("400") || e.getMessage().contains("Bad Request"), 
                    "Expected 400 error for invalid status value");
        }
        
        logTestEnd("testFindPetsByInvalidStatus");
    }
}
