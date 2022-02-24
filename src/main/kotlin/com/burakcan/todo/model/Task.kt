package com.burakcan.todo.model

import javax.persistence.*

@Entity
@Table(name = "task")
class Task(
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "description")
    var description: String?,
    @ManyToOne
    var todo: Todo
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null
}