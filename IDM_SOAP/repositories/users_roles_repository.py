from models.role_orm import Role
from models.user_orm import User
from base.sql_base import Session

def add_role_for_user(user_id, role_id):
    session = Session()
    try:
        role = session.query(Role).get(role_id)
        if role == None:
            return "Role doesn't exist!!"
        user = session.query(User).get(user_id)
        if user == None:
            return "User doesn't exist!!"
        session.query(user.roles.append(role))
        session.commit()
        return f"USER: {user.username}, ROLE added:{role.value}"
    except Exception as exc:
        print(f"Failed to add role to the user - {exc}")
    return "Unsuccesfully operation!!!"

def get_user_roles(user_id):
    session = Session()
    try:
        roles = []
        user = session.query(User).get(user_id)
        if user == None:
            return "User doesn't exist!!"
        for r in user.roles:
            roles.append(r.value)
        return roles
    except Exception as exc:
        print(f"Failed to get all the roles for the user - {exc}")
    return "Unsuccesfully operation!!!"

