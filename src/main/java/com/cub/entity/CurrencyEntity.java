package com.cub.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

@Data
@Entity
@Table(name = "CURRENCY",indexes = {@Index(name = "IDX_CURRENCY_ID",  columnList="id")})
@SequenceGenerator(initialValue = 1, allocationSize = 1, name = "GEN", sequenceName = "CURRENCY_SEQUENCE")
public class CurrencyEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** ID **/
	@Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GEN")
	private Long id;
	
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	protected Date lastModifiedDate;
	
	@Column(name="cname")
	private String cname;
	
	@Column(name="ename")
	private String ename;
	
	@Column(name="description")
	private String description;
	
	@Column(name="eur_rate", precision = 20, scale = 4)
	private BigDecimal eurRate;
	
	@Column(name="usd_rate", precision = 20, scale = 4)
	private BigDecimal usdRate;
	
	@Column(name="gbp_rate", precision = 20, scale = 4)
	private BigDecimal gbpRate;

}
