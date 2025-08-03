package utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.github.javafaker.Faker;

import static io.restassured.RestAssured.given;
import io.restassured.response.Response;
import model.dto.Category;
import model.dto.Pet;
import model.dto.Tag;

/**
 * Utility class for reusable API calls related to Pet operations
 * Updated to use proper DTO classes from model.dto package and Faker for test data
 */
public class PetHelper {
    
    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    
    private static final String[] PET_CATEGORIES = {
        "Dogs", "Cats", "Birds", "Fish", "Reptiles", "Small Animals", "Farm Animals"
    };
    
    private static final String[] PET_TAGS = {
        "friendly", "playful", "calm", "energetic", "loyal", "intelligent", 
        "protective", "social", "independent", "gentle", "active", "cuddly"
    };

    /**
     * Create a new pet
     * @param pet Pet object to create
     * @return Response of the API call
     */
    public static Response createPet(Pet pet) {
        return given()
                .header("api_key", "special-key")
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .extract()
                .response();
    }

    /**
     * Get a pet by ID
     * @param petId ID of the pet to fetch
     * @return Response of the API call
     */
    public static Response getPetById(Integer petId) {
        return given()
                .header("api_key", "special-key")
                .when()
                .get("/pet/" + petId)
                .then()
                .extract()
                .response();
    }

    /**
     * Update an existing pet
     * @param pet Pet object with updated details
     * @return Response of the API call
     */
    public static Response updatePet(Pet pet) {
        return given()
                .header("api_key", "special-key")
                .body(pet)
                .when()
                .put("/pet")
                .then()
                .extract()
                .response();
    }

    /**
     * Get a pet by ID with custom ID string (for negative testing)
     * @param petIdString String representation of the pet ID (could be invalid)
     * @return Response of the API call
     */
    public static Response getPetByIdString(String petIdString) {
        return given()
                .header("api_key", "special-key")
                .when()
                .get("/pet/" + petIdString)
                .then()
                .extract()
                .response();
    }

    /**
     * Delete a pet by ID string (for negative testing)
     * @param petIdString String representation of the pet ID (could be invalid)
     * @return Response of the API call
     */
    public static Response deletePetByIdString(String petIdString) {
        return given()
                .header("api_key", "special-key")
                .when()
                .delete("/pet/" + petIdString)
                .then()
                .extract()
                .response();
    }
    
    /**
     * Find pets by invalid status (for negative testing)
     * @param invalidStatus Invalid status value to test with
     * @return Response of the API call
     */
    public static Response findPetsByInvalidStatus(String invalidStatus) {
        return given()
                .header("api_key", "special-key")
                .queryParam("status", invalidStatus)
                .when()
                .get("/pet/findByStatus")
                .then()
                .extract()
                .response();
    }

    /**
     * Delete a pet by ID
     * @param petId ID of the pet to delete
     * @return Response of the API call
     */
    public static Response deletePet(Integer petId) {
        return given()
                .header("api_key", "special-key")
                .when()
                .delete("/pet/" + petId)
                .then()
                .extract()
                .response();
    }

    /**
     * Create a pet with invalid body for negative testing
     * @param invalidBody Invalid JSON string
     * @return Response of the API call
     */
    public static Response createPetWithInvalidBody(String invalidBody) {
        return given()
                .header("api_key", "special-key")
                .body(invalidBody)
                .when()
                .post("/pet")
                .then()
                .extract()
                .response();
    }
    
    /**
     * Update a pet with invalid body for negative testing
     * @param invalidBody Invalid JSON string
     * @return Response of the API call
     */
    public static Response updatePetWithInvalidBody(String invalidBody) {
        return given()
                .header("api_key", "special-key")
                .body(invalidBody)
                .when()
                .put("/pet")
                .then()
                .extract()
                .response();
    }

    /**
     * Create a simple Pet DTO for testing with realistic data
     * @return Pet DTO with generated data
     */
    public static Pet createSimplePet() {
        Pet pet = new Pet();
        pet.setId(faker.number().numberBetween(1, 999999));
        pet.setName(faker.dog().name());
        pet.setStatus(Pet.Status.AVAILABLE);
        pet.setPhotoUrls(Arrays.asList(generatePhotoUrl()));
        
        Category category = createRandomCategory();
        pet.setCategory(category);
        
        List<Tag> tags = createRandomTags(1, 2);
        pet.setTags(tags);
        
        return pet;
    }

    /**
     * Create a Pet DTO with full details using realistic data
     * @return Pet DTO with category and tags
     */
    public static Pet createDetailedPet() {
        Pet pet = new Pet();
        pet.setId(faker.number().numberBetween(1, 999999));
        pet.setName(faker.dog().name());
        pet.setStatus(Pet.Status.AVAILABLE);
        pet.setPhotoUrls(Arrays.asList(generatePhotoUrl(), generatePhotoUrl()));
        
        Category category = createRandomCategory();
        pet.setCategory(category);
        
        List<Tag> tags = createRandomTags(2, 4);
        pet.setTags(tags);
        
        return pet;
    }

    /**
     * Generate a realistic photo URL using picsum
     * @return Random photo URL
     */
    private static String generatePhotoUrl() {
        int imageId = faker.number().numberBetween(1, 1000);
        int width = faker.number().numberBetween(200, 800);
        int height = faker.number().numberBetween(200, 800);
        return String.format("https://picsum.photos/id/%d/%d/%d", imageId, width, height);
    }

    /**
     * Create a random category from predefined list
     * @return Category with random data
     */
    public static Category createRandomCategory() {
        String categoryName = PET_CATEGORIES[random.nextInt(PET_CATEGORIES.length)];
        Integer categoryId = faker.number().numberBetween(1, 100);
        return new Category(categoryId, categoryName);
    }

    /**
     * Create 2-3 random tags from predefined list
     * @return List of random tags
     */
    public static List<Tag> createRandomTags() {
        return createRandomTags(2, 3);
    }

    /**
     * Create random tags from predefined list with custom count range
     * @param minCount Minimum number of tags
     * @param maxCount Maximum number of tags
     * @return List of random tags
     */
    public static List<Tag> createRandomTags(int minCount, int maxCount) {
        int tagCount = faker.number().numberBetween(minCount, maxCount + 1);
        List<Tag> tags = new java.util.ArrayList<>();
        
        for (int i = 0; i < tagCount; i++) {
            String tagName = PET_TAGS[random.nextInt(PET_TAGS.length)];
            Integer tagId = faker.number().numberBetween(1, 50 + i);
            tags.add(new Tag(tagId, tagName));
        }
        
        return tags;
    }
}
