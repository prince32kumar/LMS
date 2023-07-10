
package dash.productmanagement.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;

import dash.common.AbstractWorkflow;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "OrderPosition", description = "OrderPosition")
@Entity
@SQLDelete(sql = "UPDATE orderposition SET deleted = '1' WHERE id = ?")
@Where(clause = "deleted <> '1'")
@Table(name = "orderposition")
public class OrderPosition {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "orderposition_auto_gen")
	@SequenceGenerator(name = "orderposition_auto_gen", sequenceName = "orderposition_id_seq", allocationSize = 1)
	@ApiModelProperty(hidden = true)
	@Column(name = "id", nullable = false)
	private Long id;

	@ApiModelProperty(hidden = true)
	@NotNull
	@Column(name = "deleted", nullable = false)
	private boolean deleted;

	@ManyToOne
	@JoinColumn(name = "product_fk", nullable = false)
	// @Where(clause = "deleted <> '1'")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "workflow_fk", nullable = false)
	@Where(clause = "deleted <> '1'")
	@JsonBackReference("orderPositions-abstractWorkflow")
	private AbstractWorkflow workflow;

	@NotNull
	@Column(name = "amount", nullable = false)
	private Integer amount;

	@ApiModelProperty(hidden = true)
	@NotNull
	@Digits(integer = 10, fraction = 4)
	@Column(name = "net_price", nullable = false)
	private Double netPrice;

	@NotNull
	@Column(name = "discount", nullable = false)
	private Double discount;

	public OrderPosition() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Product getProduct() {
		return product;
	}

	@ApiModelProperty(value = "product", dataType = "dash.productmanagement.domain.Product", required = true)
	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public AbstractWorkflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(AbstractWorkflow workflow) {
		this.workflow = workflow;
	}

	public Double getNetPrice() {
		return netPrice;
	}

	public void setNetPrice(Double price) {
		this.netPrice = price;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + (deleted ? 1231 : 1237);
		result = prime * result + ((discount == null) ? 0 : discount.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((netPrice == null) ? 0 : netPrice.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((workflow == null) ? 0 : workflow.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderPosition other = (OrderPosition) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (deleted != other.deleted)
			return false;
		if (discount == null) {
			if (other.discount != null)
				return false;
		} else if (!discount.equals(other.discount))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (netPrice == null) {
			if (other.netPrice != null)
				return false;
		} else if (!netPrice.equals(other.netPrice))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (workflow == null) {
			if (other.workflow != null)
				return false;
		} else if (!workflow.equals(other.workflow))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderPosition [id=" + id + ", deleted=" + deleted + ", amount=" + amount + ", price=" + netPrice
				+ ", discount=" + discount + "]";
	}

}
