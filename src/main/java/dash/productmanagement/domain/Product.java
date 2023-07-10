
package dash.productmanagement.domain;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.SQLDelete;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import dash.consistencymanagement.domain.ConsistencyObject;
import dash.fileuploadmanagement.domain.FileUpload;
import io.swagger.annotations.ApiModelProperty;

@Entity
@SQLDelete(sql = "UPDATE product SET deleted = '1' WHERE id = ?")
// @Where(clause = "deleted <> '1'")
@Table(name = "product")
@SequenceGenerator(name = "idgen", sequenceName = "product_id_seq", allocationSize = 1)
public class Product extends ConsistencyObject implements Serializable {

	private static final long serialVersionUID = 2316129901873904110L;

	// For Olap Deserialization
	@Transient
	private Long id;
	
	@ApiModelProperty(hidden = true)
	@NotNull
	@Size(max = 100)
	@Column(name = "name", nullable = false)
	private String name;

	@ApiModelProperty(hidden = true)
	@Size(max = 3000)
	@Column(name = "description", length = 3000, nullable = true)
	private String description;

	@Enumerated(EnumType.STRING)
	@ApiModelProperty(hidden = true)
	@Column(name = "productstate", length = 255, nullable = true)
	private ProductState productState;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss:SSS")
	@ApiModelProperty(hidden = true)
	@NotNull
	@Column(name = "timestamp", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar timestamp;

	@ApiModelProperty(hidden = true)
	@NotNull
	@Column(name = "deactivated", nullable = false)
	private boolean deactivated;

	@ApiModelProperty(hidden = true)
	@NotNull
	@Digits(integer = 10, fraction = 4)
	@Column(name = "net_price", nullable = false)
	private Double netPrice;

	@ApiModelProperty(hidden = true)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "picture_fk", nullable = true)
	private FileUpload picture;

	@ApiModelProperty(hidden = true)
	@Size(max = 255)
	@Column(name = "productnumber")
	private String productNumber;

	@JsonIgnore
	private String getBase64ImgTag() {
		if (getPicture() == null || getPicture().getContent() == null)
			return null;
		String base64String = org.apache.commons.codec.binary.Base64.encodeBase64String(getPicture().getContent());
		return "<img src=\"data:image/jpeg;base64," + base64String;
	}

	@JsonIgnore
	public String getProductPictureSmall() {
		String base64ImgTag = getBase64ImgTag();
		return base64ImgTag == null ? null : base64ImgTag + "\" width=\"50\" style=\"max-width:50px\">";
	}

	@JsonIgnore
	public String getProductPictureMedium() {
		String base64ImgTag = getBase64ImgTag();
		return base64ImgTag == null ? null : base64ImgTag + "\" width=\"100\" style=\"max-width:100px\">";
	}

	@JsonIgnore
	public String getProductPictureBig() {
		String base64ImgTag = getBase64ImgTag();
		return base64ImgTag == null ? null : base64ImgTag + "\" width=\"200\" style=\"max-width:200px\">";
	}

	public Product() {
	}

	public ProductState getProductState() {
		return productState;
	}

	@ApiModelProperty(hidden = true)
	public String getProductStateGermanTranslation() {
		return productState == null ? null : productState.getGermanTranslation();
	}

	@ApiModelProperty(hidden = true)
	public String getProductStateEnglishTranslation() {
		return productState == null ? null : productState.getEnglishTranslation();
	}

	public void setProductState(ProductState productState) {
		this.productState = productState;
	}

	public boolean isDeactivated() {
		return deactivated;
	}

	public void setDeactivated(boolean deactivated) {
		this.deactivated = deactivated;
	}

	public Calendar getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}

	public FileUpload getPicture() {
		return picture;
	}

	public void setPicture(FileUpload picture) {
		this.picture = picture;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(Double priceNetto) {
		this.netPrice = priceNetto;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (deactivated ? 1231 : 1237);
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((netPrice == null) ? 0 : netPrice.hashCode());
		result = prime * result + ((picture == null) ? 0 : picture.hashCode());
		result = prime * result + ((productNumber == null) ? 0 : productNumber.hashCode());
		result = prime * result + ((productState == null) ? 0 : productState.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (deactivated != other.deactivated)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (netPrice == null) {
			if (other.netPrice != null)
				return false;
		} else if (!netPrice.equals(other.netPrice))
			return false;
		if (picture == null) {
			if (other.picture != null)
				return false;
		} else if (!picture.equals(other.picture))
			return false;
		if (productNumber == null) {
			if (other.productNumber != null)
				return false;
		} else if (!productNumber.equals(other.productNumber))
			return false;
		if (productState != other.productState)
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [name=" + name + ", productState=" + productState + ", timestamp=" + timestamp
				+ ", deactivated=" + deactivated + ", priceNetto=" + netPrice + ", picture=" + picture + "]";
	}

}
