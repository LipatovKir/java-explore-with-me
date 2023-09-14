package ru.practicum.comments.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.proxy.HibernateProxy;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

import static ru.practicum.Constant.DATE_FORMAT;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "comments", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @ToString.Exclude
    Event event;
    @Size(min = 20, max = 500)
    @Column(name = "message")
    String message;
    @Column(name = "created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    LocalDateTime created;

    @Override
    public final boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (otherObject == null) {
            return false;
        }
        Class<?> otherEffectiveClass = otherObject instanceof HibernateProxy
                ? ((HibernateProxy) otherObject).getHibernateLazyInitializer().getPersistentClass()
                : otherObject.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        return thisEffectiveClass == otherEffectiveClass
                && getId() != null
                && Objects.equals(getId(), ((Comment) otherObject).getId());
    }

    @Override
    public final int hashCode() {
        if (this instanceof HibernateProxy) {
            HibernateProxy hibernateProxy = (HibernateProxy) this;
            return hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode();
        } else {
            return getClass().hashCode();
        }
    }
}