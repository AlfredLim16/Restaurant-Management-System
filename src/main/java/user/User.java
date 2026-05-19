package user;

public class User {

        private String userName;
        private String userPassword;
        private Role userRole;

        public User(){

        }

        public User(String userName, String userPassword, Role userRole){
            this.userName = userName;
            this.userPassword = userPassword;
            this.userRole = userRole;
        }

        public String getUserName(){
            return userName;
        }
        public void setUserName(String userName){
            this.userName = userName;
        }

        public String getUserPassword(){
            return userPassword;
        }
        public void setUserPassword(String userPassword){
            this.userPassword = userPassword;
        }

        public Role getUserRole(){
            return userRole;
        }
        public void setUserRole(Role userRole){
            this.userRole = userRole;
        }
    }