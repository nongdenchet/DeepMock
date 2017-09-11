package com.rain.deepmock;

import com.deepmock.DeepMock;
import com.deepmock.annotations.Mock;
import com.deepmock.annotations.Stub;
import com.deepmock.annotations.Target;
import com.rain.deepmock.data.User;
import com.rain.deepmock.data.UserEntity;
import com.rain.deepmock.data.UserRepo;
import com.rain.deepmock.data.UserStore;
import com.rain.deepmock.schedulers.Schedulers;
import com.rain.deepmock.schedulers.TestSchedulers;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class UserRepoTest {
    @Stub
    Schedulers schedulers = new TestSchedulers();
    @Mock
    UserStore userStore;
    @Target
    UserRepo userRepo;

    @Before
    public void setUp() throws Exception {
        DeepMock.inject(this);
        when(userStore.getUsers()).thenReturn(Arrays.asList(
                new UserEntity("name1", "name1@gmail.com"),
                new UserEntity("name2", "name2@gmail.com")
        ));
    }

    @Test
    public void getUsers_returnCorrectSize() throws Exception {
        List<User> users = userRepo.getUsers();
        assertEquals(2, users.size());
    }

    @Test
    public void getUsers_returnCorrectNames() throws Exception {
        List<User> users = userRepo.getUsers();
        assertEquals("name1", users.get(0).getUsername());
        assertEquals("name2", users.get(1).getUsername());
    }

    @Test
    public void getUsers_returnCorrectEmails() throws Exception {
        List<User> users = userRepo.getUsers();
        assertEquals("name1@gmail.com", users.get(0).getEmail());
        assertEquals("name2@gmail.com", users.get(1).getEmail());
    }
}