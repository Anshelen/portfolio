package dev.shelenkov.portfolio.repository.support;

import dev.shelenkov.portfolio.domain.Role;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Hibernate type for {@code Set<Role>} that is mapped to enum array.
 */
@SuppressWarnings({"unchecked", "unused", "RedundantSuppression"})
public class SetOfRolesType implements UserType {

    @Override
    public int[] sqlTypes() {
        return new int[] {Types.ARRAY};
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class<Set> returnedClass() {
        return Set.class;
    }

    @Override
    public boolean equals(Object x, Object y) {
        if (x == null) {
            return y == null;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names,
                              SharedSessionContractImplementor session,
                              Object owner) throws SQLException {

        Array array = rs.getArray(names[0]);
        if (rs.wasNull()) {
            return EnumSet.noneOf(Role.class);
        }
        String[] data = (String[]) array.getArray();
        return Arrays.stream(data)
            .map(Role::valueOf)
            .collect(Collectors.toSet());
    }

    @SuppressWarnings("resource")
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
                            SharedSessionContractImplementor session)
        throws SQLException {

        Connection connection = st.getConnection();
        Set<Role> castObject;
        if (value == null) {
            castObject = EnumSet.noneOf(Role.class);
        } else {
            castObject = (Set<Role>) value;
        }
        Array array = connection.createArrayOf("user_role", castObject.toArray());
        st.setArray(index, array);
    }

    @SuppressWarnings("ReturnOfNull")
    @Override
    public Object deepCopy(Object value) {
        if (value == null) {
            return null;
        }
        return EnumSet.copyOf((Collection<Role>) value);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) {
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) {
        return deepCopy(original);
    }
}
