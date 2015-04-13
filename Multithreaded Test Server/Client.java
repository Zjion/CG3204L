class Client {
  private String name;
  private String ip;
  private String password;
  
  public Client(String name, String password, String ip)
  {
    this.name = name;
    this.password = password;
    this.ip = ip;
  }
  
  public String getName() {return name;}
  public String getIP() {return ip;}
  public String getPassword() {return password;}
  
  public void setName(String name) {this.name = name;}
  public void setPassword(String password) {this.password = password;}
  public void setIP(String IP) {this.ip = ip;}
}