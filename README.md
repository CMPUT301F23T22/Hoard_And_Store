# Hoard_And_Store

![image](https://github.com/CMPUT301F23T22/Hoard_And_Store/assets/73729890/3b97d586-58ed-4bda-8308-0ae04c29819f)

## Overview
This document outlines the design and implementation of a simple, attractive, and easy-to-use Android application for inventory management. This application aims to help users efficiently record and manage items for various purposes, such as insurance. The design is flexible to allow future developers to extend or migrate it.

## WIKI (https://github.com/CMPUT301F23T22/Hoard_And_Store/wiki)

## User Stories

### Items
- **US 01.01.01**: As an owner, I want to add an item to my items, with a date of purchase or acquisition, brief description, make, model, serial number (if applicable), estimated value, and comment.
- **US 01.02.01**: As an owner, I want to view an item and its details.
- **US 01.03.01**: As an owner, I want to edit the details of an item.
- **US 01.04.01**: As an owner, I want to delete an item.

### List of Items
- **US 02.01.01**: As an owner, I want to see a list of my items.
- **US 02.02.01**: As an owner, I want to see the total estimated value of the shown items in the list of items.
- **US 02.03.01**: As an owner, I want to select items from the list of items and delete the selected items.
- **US 02.04.01**: As an owner, I want to sort the list of items by date, description, make, or estimated value by ascending or descending order.
- **US 02.05.01**: As an owner, I want to filter the list of items by date range.
- **US 02.06.01**: As an owner, I want to filter the list of items by description keywords.
- **US 02.07.01**: As an owner, I want to filter the list of items by make.

### Tags
- **US 03.01.01**: As an owner, I want to define tags that can be used to categorize items.
- **US 03.02.01**: As an owner, I want to specify one or more tags for an item.
- **US 03.03.01**: As an owner, I want to select items from the list of items and apply one or more tags to the selected items.
- **US 03.04.01**: As an owner, I want to sort the list of items by tags.
- **US 03.05.01**: As an owner, I want to filter the list of items by tags.

### Photographs
- **US 04.01.01**: As an owner, I want to take photos using the camera from the app, and attach one or more photographs to an item of mine.
- **US 04.02.01**: As an owner, I want to use photos from my photo gallery, and attach one or more photographs to an item of mine.
- **US 04.03.01**: As an owner, I want to delete an attached photograph for an item of mine.

### Scanning
- **US 05.01.01**: As an owner, I want help to specify the serial number for an item by automatically identifying the number from an image taken by the camera.
- **US 05.02.01**: As an owner, I want help to specify the description for an item by automatically identifying the product barcode from an image taken by the camera and using associated product information.

### User Profile
- **US 06.01.01**: As a user, I want a profile with a unique username.
