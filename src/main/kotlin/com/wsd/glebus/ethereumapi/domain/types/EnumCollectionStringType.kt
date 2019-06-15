package com.wsd.glebus.ethereumapi.domain.types

import com.wsd.glebus.ethereumapi.domain.Role
import org.apache.commons.lang.StringUtils
import org.hibernate.HibernateException
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.usertype.UserType
import java.io.Serializable
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Types

/**
 * @see <a href="https://habr.com/ru/post/91328/">Пользовательские типы в Hibernate</a>
 */
class EnumCollectionStringType : UserType {
    override fun sqlTypes(): IntArray {
        return intArrayOf(Types.VARCHAR)
    }

    override fun returnedClass(): Class<*> {
        return Set::class.java
    }

    //ToDO: implement logic better
    @Throws(HibernateException::class)
    override fun equals(x: Any, y: Any): Boolean {
        return x === y
    }

    @Throws(HibernateException::class)
    override fun hashCode(x: Any): Int {
        return x.hashCode()
    }

    @Throws(HibernateException::class)
    override fun nullSafeSet(st: PreparedStatement, value: Any?, index: Int, session: SharedSessionContractImplementor) {
        try {
            if (null == value) {
                st.setNull(index, Types.VARCHAR)
            } else {
                st.setString(index, toCommaSeparated((value as Set<Enum<Role>>?)!!))
            }
        } catch (e: Exception) {
            throw HibernateException(String.format("Exception while stringifing '%s'", value), e)
        }
    }

    private fun toCommaSeparated(inputSet: Set<Enum<*>>): String {
        val sb = StringBuilder()
        var delim = ""
        for (current in inputSet) {
            sb.append(delim).append(current.name)
            delim = ","
        }
        return sb.toString()
    }

    @Throws(HibernateException::class, SQLException::class)
    override fun nullSafeGet(rs: ResultSet, names: Array<out String>, session: SharedSessionContractImplementor?, owner: Any): Any {
        val name = rs.getString(names[0])
        if (rs.wasNull()) {
            throw IllegalStateException()
        }

        try {
            val retSet = mutableSetOf<Role>()
            if (StringUtils.isNotBlank(name)) {
                val values = name.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (value in values) {
                    val current = getEnumValueFor(value)
                    if (current != null) {
                        retSet.add(current)
                    }
                }
            }
            return retSet
        } catch (e: Exception) {
            throw HibernateException("Exception getting object from comma separated string", e)
        }
    }

    private fun getEnumValueFor(name: String): Role? {
        for (current in Role.values()) {
            if (current.name == name) {
                return current
            }
        }
        return null
    }

    override fun isMutable(): Boolean {
        return false
    }

    override fun deepCopy(value: Any): Any {
        return value
    }

    @Throws(HibernateException::class)
    override fun disassemble(value: Any): Serializable {
        return value as Serializable
    }

    @Throws(HibernateException::class)
    override fun assemble(cached: Serializable, owner: Any): Any {
        return cached
    }

    @Throws(HibernateException::class)
    override fun replace(original: Any, target: Any, owner: Any): Any {
        return original
    }

}