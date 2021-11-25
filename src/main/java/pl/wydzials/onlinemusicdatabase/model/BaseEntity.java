package pl.wydzials.onlinemusicdatabase.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class BaseEntity implements Serializable {

  protected static final ToStringStyle TO_STRING_STYLE = ToStringStyle.SHORT_PREFIX_STYLE;

  @Id
  @GeneratedValue
  private Long id;

  private String uuid = UUID.randomUUID().toString();

  public Long getId() {
    return id;
  }

  private void setId(final Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(final Object object) {
    return object instanceof BaseEntity
        && Objects.equals(((BaseEntity) object).uuid, uuid);
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, TO_STRING_STYLE)
        .append("id", id)
        .build();
  }
}
