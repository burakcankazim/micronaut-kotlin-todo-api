package com.burakcan.todo.model

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import javax.persistence.*

@Entity
@Table(name = "todo")
class Todo(
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "description")
    var description: String?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id", nullable = false)
    var id: Long? = null

    @OneToMany(
        fetch = FetchType.EAGER,
        mappedBy = "todo",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    var tasks: MutableList<Task> = mutableListOf()
}