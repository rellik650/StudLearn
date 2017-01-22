package dao;

import model.Address;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class AddressDAOImpl implements AddressDAO {

    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    public AddressDAOImpl(DataSource dataSource) {
        setDataSource(dataSource);  //injected via Spring
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /*Generates a new primary key for the address table based*/
    public int generateId() {
        String sql = "SELECT MAX(address_id) AS id FROM address";
        return jdbcTemplate.query(sql, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if(resultSet.next()) {
                    int addressID = resultSet.getInt("id");
                    return addressID + 1;
                }
                return 1;
            }
        });
    }

    /*CRUD operations*/

    /*Create*/
    public void create(Address address) {
        String sql = "INSERT INTO address VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, address.getAddressId(), address.getStreetName(), address.getStreetNumber(),
                address.getCity(), address.getState());
    }

    /*Read*/
    public Address findById(int addressId) {
        String sql = "SELECT * FROM address WHERE address_id = " + addressId;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Address>() {
            public Address extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if(resultSet.next()) {
                    Address address;
                    int addressId = resultSet.getInt("address_id");
                    String streetName = resultSet.getString("street_name");
                    int streetNumber = resultSet.getInt("street_number");
                    String city = resultSet.getString("city");
                    String state = resultSet.getString("state");
                    address = new Address(addressId, streetName, streetNumber, city, state);
                    return address;
                }
                return null;
            }
        });
    }

    /*Update*/
    public void update(Address address) {
        String sql = "UPDATE address SET street_name = ?, street_number = ?, city = ?, state = ? WHERE address_id = ?";
        jdbcTemplate.update(sql, address.getStreetName(), address.getStreetNumber(), address.getCity(),
                address.getState(),  address.getAddressId());
    }

    /*Delete*/
    public void delete(int addressId) {
        String sql = "DELETE FROM address WHERE address_id = ?";
        jdbcTemplate.update(sql, addressId);
    }

    /* public List<Address> findAll() {
        String sql = "SELECT * FROM address";
        List<Address> addressList = jdbcTemplate.query(sql, new RowMapper<Address>() {

            public Address mapRow(ResultSet resultSet, int i) throws SQLException {
                int addressId = resultSet.getInt("address_id");
                String streetName = resultSet.getString("street_name");
                int streetNumber = resultSet.getInt("street_number");
                String city = resultSet.getString("city");
                String state = resultSet.getString("state");
                Address address = new Address(addressId, streetName, streetNumber, city, state);
                return address;
            }
        });
        return addressList;
    } */

}
