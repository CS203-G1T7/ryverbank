// package cs203.g1t7.account;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// @ExtendWith(MockitoExtension.class)
// public class AccountServiceTest {
//     @Mock 
//     private AccountRepository accounts; 

//     @InjectMocks
//     private AccountServiceImpl accountService;
    
    
//     @Test
//     void addAccount_NewAccount_ReturnSavedAccount(){
//         // arrange ***
//         Account account = new Account(2, 50000, 50000);

//         // mock the "save" operation 
//         when(accounts.save(any(Account.class))).thenReturn(account);

//         // act ***
//         Account savedAccount = accountService.addAccount(account);
        
//         // assert ***
//         assertNotNull(savedAccount);
//         verify(accounts).save(account);
//     }

//     @Test
//     void listAccount_ReturnAllAccounts(){
//         List<Account> listAccounts = accountService.listAccounts();
//         verify(accounts).findAll();
//     }
// }