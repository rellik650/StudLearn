package dao;

import model.Address;
import java.util.List;


public interface AddressDAO {

    /*CRUD methods*/
    void create(Address address);

    Address findById(int addressId);

    void update(Address address);

    void delete(int addressId);

    //List<Address> findAll();

    /*Other methods used to */
    int generateId();
}
