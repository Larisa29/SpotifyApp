import hashlib
import re

#password must have at least 8 characters, 1 uppercase, 1 lowercase, one digit and one special character
pattern = r"^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$"

def check_password_is_valid(password):
    if re.match(pattern, password):
        return True

    return False

def encode_password(password):

    hashed_password = hashlib.sha256(password.encode('utf-8')).hexdigest()

    return hashed_password

def password_match(password, hashed_password):
    encoded_password = encode_password(password)
    return hashed_password == encoded_password