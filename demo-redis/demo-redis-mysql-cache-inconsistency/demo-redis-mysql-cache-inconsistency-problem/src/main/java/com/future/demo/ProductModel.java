package com.future.demo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author dexterleslie@gmail.com
 */
@Data
@Entity
@Table(name="t_product")
public class ProductModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -455392667421971562L;

	@Id
	private long id;
	private int quantity;
	private Date createTime;
}
