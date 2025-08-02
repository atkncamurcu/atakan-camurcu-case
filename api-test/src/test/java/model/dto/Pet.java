package model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pet DTO for Swagger Petstore API
 * Based on official Swagger schema with all required and optional fields
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pet {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("category")
    private Category category;

    @JsonProperty("name")
    private String name; // Required field

    @JsonProperty("photoUrls")
    private List<String> photoUrls; // Required field

    @JsonProperty("tags")
    private List<Tag> tags;

    @JsonProperty("status")
    private String status; // Enum: available, pending, sold

    public Pet() {
        this.photoUrls = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public Pet(String name, List<String> photoUrls) {
        this.name = name;
        this.photoUrls = photoUrls != null ? photoUrls : new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public Pet(Integer id, Category category, String name, List<String> photoUrls, 
               List<Tag> tags, String status) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.photoUrls = photoUrls != null ? photoUrls : new ArrayList<>();
        this.tags = tags != null ? tags : new ArrayList<>();
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls != null ? photoUrls : new ArrayList<>();
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Enum constants for pet status
     */
    public static class Status {
        public static final String AVAILABLE = "available";
        public static final String PENDING = "pending";
        public static final String SOLD = "sold";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return Objects.equals(id, pet.id) &&
               Objects.equals(category, pet.category) &&
               Objects.equals(name, pet.name) &&
               Objects.equals(photoUrls, pet.photoUrls) &&
               Objects.equals(tags, pet.tags) &&
               Objects.equals(status, pet.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, name, photoUrls, tags, status);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", category=" + category +
                ", name='" + name + '\'' +
                ", photoUrls=" + photoUrls +
                ", tags=" + tags +
                ", status='" + status + '\'' +
                '}';
    }
}
