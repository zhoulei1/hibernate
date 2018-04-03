package zl.example;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@DynamicInsert
@DynamicUpdate
@Entity
public class T implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	//@Column(name = "id", nullable = false, unique = true)
    @GenericGenerator(
            name = "sequenceGenerator", 
            strategy = "enhanced-sequence",
            parameters = {
                @org.hibernate.annotations.Parameter(
                    name = "optimizer",
                    value = "pooled-lo"
                ),
                @org.hibernate.annotations.Parameter(
                    name = "initial_value", 
                    value = "1"
                ),
                @org.hibernate.annotations.Parameter(
                    name = "increment_size", 
                    value = "5"
                )
            }
        )
        @GeneratedValue(
            strategy = GenerationType.SEQUENCE, 
            generator = "sequenceGenerator"
        )
    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}

