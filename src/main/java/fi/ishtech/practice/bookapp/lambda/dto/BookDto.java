package fi.ishtech.practice.bookapp.lambda.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import fi.ishtech.practice.bookapp.lambda.utils.DynamoDbUtil;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.utils.StringUtils;

/**
 * DTO for Book
 *
 * @author Muneer Ahmed Syed
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDto implements Serializable {

	@Serial
	private static final long serialVersionUID = -2802652510431885318L;

	@JsonIgnore
	public static final String ID = "id";

	@JsonIgnore
	public static final String TITLE = "title";

	@JsonIgnore
	public static final String AUTHOR = "author";

	@JsonIgnore
	public static final String YEAR = "year";

	@JsonIgnore
	public static final String PRICE = "price";

	private String id;
	private String title;
	private String author;
	private Short year;
	private BigDecimal price;

	public BookDto() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Short getYear() {
		return year;
	}

	public void setYear(Short year) {
		this.year = year;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}