from models.user_orm import User
from base.sql_base import Session

def get_users():
    session = Session()
    users = session.query(User).all()
    return users

def get_user_by_id(id):
    session = Session()
    user = session.query(User).get(id)
    return user

def get_user_by_username(username):
    session = Session()
    try:
        result = session.query(User).filter(User.username == username).first()
    except Exception as exc:
        print(f"Failed to GET user - {exc}")
    return result

def create_user(username, password):
    session = Session()
    check_user = session.query(User).filter_by(username=username, password=password).first()
    if check_user:
        return f"User with username={username} and password={password} already exists!"
    user = User(username, password)
    try:
        session.add(user)
        session.commit()
    except Exception as exc:
        print(f"Failed to add user - {exc}")
    return f"id:{user.id}, username:{user.username}, password:{user.password}"

def delete_user_by_username(username):
    session = Session()
    try:
        session.query(User).filter(User.username == username).delete(synchronize_session= False)
        session.commit()
    except Exception as exc:
        print(f"Failed to DELETE user - {exc}")
    return f"User {username } deletd!"

def update_username_for_user(id, updated_name):
    session = Session()
    if (get_user_by_id(id) == None):
        return f"User doesn't exist!"
    session.query(User).filter(User.id == id).update({User.username: updated_name})
    session.commit()
    updated_user = get_user_by_id(id)
    return f"User updated: id:{updated_user.id}, username:{updated_user.username}"

def update_password_for_user(id, updated_password):
    session = Session()
    if (get_user_by_id(id) == None):
        return f"User doesn't exist!"
    session.query(User).filter(User.id == id).update({User.password: updated_password})
    session.commit()
    updated_user = get_user_by_id(id)
    return f"The user's password has been successfully updated! \n\t\t\t id:{updated_user.id}, password:{updated_user.password}"

def get_user_info(user_id):
    session = Session()
    try:
        user = session.query(User).get(user_id)
        if user == None:
            return "User doesn't exist!!"
        s = "Id:" + str(user.id) + "\n\t\t\t\t\t\t Username:" + user.username + "\n\t\t\t\t\t\t Password:" + user.password + "\n\t\t\t\t\t\t Roles:["
        for r in user.roles:
           s += r.value + ","
        s += "] \n\t\t\t\t"
        return s
    except Exception as exc:
        print(f"Failed to get all info for the user - {exc}")
    return "Unsuccesfully operation!!!"

def get_all_users_info():
    session = Session()
    users = session.query(User).all()
    results = []
    for u in users:
        s = "Id:"+ str(u.id) +"\n\t\t\t\t\t\t Username:" + u.username + "\n\t\t\t\t\t\t Password:" + u.password + "\n\t\t\t\t\t\t Roles:["
        for r in u.roles:
            s += r.value + ","
        s += "] \n\t\t\t\t"
        results.append(s)
    return results